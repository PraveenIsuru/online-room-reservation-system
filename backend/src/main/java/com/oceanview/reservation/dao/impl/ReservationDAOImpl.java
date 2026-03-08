package com.oceanview.reservation.dao.impl;

import com.oceanview.reservation.dao.ReservationDAO;
import com.oceanview.reservation.dto.ReservationListDTO;
import com.oceanview.reservation.model.Reservation;
import com.oceanview.reservation.model.enums.BillStatus;
import com.oceanview.reservation.model.enums.ReservationStatus;
import com.oceanview.reservation.model.enums.RoomType;
import com.oceanview.reservation.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

public class ReservationDAOImpl implements ReservationDAO {

    @Override
    public int getNextSequenceForYear(int year) {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(reservation_number, 10) AS UNSIGNED)), 0) + 1 AS next_seq " +
                     "FROM reservations " +
                     "WHERE reservation_number LIKE CONCAT('RES-', ?, '-%')";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_seq");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservation sequence", e);
        }
        return 1;
    }

    @Override
    public int create(Reservation r) {
        String sql = "INSERT INTO reservations (reservation_number, guest_id, room_id, created_by, check_in_date, check_out_date, status, special_requests) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getReservationNumber());
            ps.setInt(2, r.getGuestId());
            ps.setInt(3, r.getRoomId());
            ps.setInt(4, r.getCreatedBy());
            ps.setDate(5, Date.valueOf(r.getCheckInDate()));
            ps.setDate(6, Date.valueOf(r.getCheckOutDate()));
            ps.setString(7, r.getStatus().name());
            ps.setString(8, r.getSpecialRequests());
            int affected = ps.executeUpdate();
            if (affected == 0) throw new RuntimeException("Creating reservation failed, no rows affected.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new RuntimeException("Creating reservation failed, no ID obtained.");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating reservation", e);
        }
    }

    @Override
    public boolean updateStatus(int reservationId, ReservationStatus status) {
        String sql = "UPDATE reservations SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, reservationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating reservation status", e);
        }
    }

    @Override
    public boolean existsRoomConflict(int roomId, java.time.LocalDate checkIn, java.time.LocalDate checkOut, Integer excludeReservationId) {
        String base = "SELECT 1 FROM reservations WHERE room_id = ? AND status IN ('PENDING','CONFIRMED','CHECKED_IN') " +
                      "AND NOT (check_out_date <= ? OR check_in_date >= ?)" +
                      (excludeReservationId != null ? " AND reservation_id <> ?" : "") +
                      " LIMIT 1";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(base)) {
            ps.setInt(1, roomId);
            ps.setDate(2, Date.valueOf(checkIn));
            ps.setDate(3, Date.valueOf(checkOut));
            if (excludeReservationId != null) ps.setInt(4, excludeReservationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking room conflict", e);
        }
    }

    @Override
    public Reservation findById(int reservationId) {
        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservation by id", e);
        }
    }

    @Override
    public Reservation findByNumber(String reservationNumber) {
        String sql = "SELECT * FROM reservations WHERE reservation_number = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservation by number", e);
        }
    }

    @Override
    public int getActiveReservationCount() {
        String sql = "SELECT COUNT(*) FROM reservations WHERE status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting active reservations", e);
        }
    }

    @Override
    public java.util.List<Reservation> findAll() {
        String sql = "SELECT * FROM reservations";
        java.util.List<Reservation> list = new java.util.ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all reservations", e);
        }
    }

    @Override
    public java.util.List<Reservation> findByCheckInDate(java.time.LocalDate date) {
        String sql = "SELECT * FROM reservations WHERE check_in_date = ?";
        java.util.List<Reservation> list = new java.util.ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservations by check-in date", e);
        }
    }

    @Override
    public java.util.List<Reservation> findByCheckOutDate(java.time.LocalDate date) {
        String sql = "SELECT * FROM reservations WHERE check_out_date = ?";
        java.util.List<Reservation> list = new java.util.ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservations by check-out date", e);
        }
    }

    @Override
    public java.util.List<Reservation> findWithPagination(int offset, int limit, com.oceanview.reservation.model.enums.ReservationStatus status) {
        String sql = "SELECT * FROM reservations";
        if (status != null) {
            sql += " WHERE status = ?";
        }
        sql += " ORDER BY created_at DESC LIMIT ? OFFSET ?";

        java.util.List<Reservation> list = new java.util.ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (status != null) {
                ps.setString(paramIndex++, status.name());
            }
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservations with pagination", e);
        }
    }

    @Override
    public int countReservations(com.oceanview.reservation.model.enums.ReservationStatus status) {
        String sql = "SELECT COUNT(*) FROM reservations";
        if (status != null) {
            sql += " WHERE status = ?";
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (status != null) {
                ps.setString(1, status.name());
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting reservations", e);
        }
    }

    @Override
    public java.util.List<ReservationListDTO> findEnrichedWithPagination(int offset, int limit, ReservationStatus status, String search, Integer guestId) {
        StringBuilder sql = new StringBuilder(
            "SELECT r.reservation_id, r.reservation_number, r.guest_id, g.guest_name, g.contact_number, " +
            "r.room_id, rm.room_number, rm.room_type, rm.price_per_night, " +
            "r.check_in_date, r.check_out_date, r.status, r.special_requests, " +
            "b.bill_id, b.total_amount, b.status AS bill_status, " +
            "r.created_at, u.username AS created_by_username " +
            "FROM reservations r " +
            "LEFT JOIN guests g ON r.guest_id = g.guest_id " +
            "LEFT JOIN rooms rm ON r.room_id = rm.room_id " +
            "LEFT JOIN bills b ON r.reservation_id = b.reservation_id " +
            "LEFT JOIN users u ON r.created_by = u.user_id " +
            "WHERE 1=1"
        );

        java.util.List<Object> params = new java.util.ArrayList<>();
        if (status != null) {
            sql.append(" AND r.status = ?");
            params.add(status.name());
        }
        if (guestId != null) {
            sql.append(" AND r.guest_id = ?");
            params.add(guestId);
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (g.guest_name LIKE ? OR r.reservation_number LIKE ? OR g.contact_number LIKE ?)");
            String pattern = "%" + search.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        sql.append(" ORDER BY r.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        java.util.List<ReservationListDTO> list = new java.util.ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) ps.setString(i + 1, (String) param);
                else if (param instanceof Integer) ps.setInt(i + 1, (Integer) param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEnriched(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching enriched reservations with pagination", e);
        }
    }

    @Override
    public int countEnrichedReservations(ReservationStatus status, String search, Integer guestId) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM reservations r " +
            "LEFT JOIN guests g ON r.guest_id = g.guest_id " +
            "WHERE 1=1"
        );

        java.util.List<Object> params = new java.util.ArrayList<>();
        if (status != null) {
            sql.append(" AND r.status = ?");
            params.add(status.name());
        }
        if (guestId != null) {
            sql.append(" AND r.guest_id = ?");
            params.add(guestId);
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (g.guest_name LIKE ? OR r.reservation_number LIKE ? OR g.contact_number LIKE ?)");
            String pattern = "%" + search.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) ps.setString(i + 1, (String) param);
                else if (param instanceof Integer) ps.setInt(i + 1, (Integer) param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting enriched reservations", e);
        }
    }

    @Override
    public ReservationListDTO findEnrichedById(int reservationId) {
        String sql = "SELECT r.reservation_id, r.reservation_number, r.guest_id, g.guest_name, g.contact_number, " +
            "r.room_id, rm.room_number, rm.room_type, rm.price_per_night, " +
            "r.check_in_date, r.check_out_date, r.status, r.special_requests, " +
            "b.bill_id, b.total_amount, b.status AS bill_status, " +
            "r.created_at, u.username AS created_by_username " +
            "FROM reservations r " +
            "LEFT JOIN guests g ON r.guest_id = g.guest_id " +
            "LEFT JOIN rooms rm ON r.room_id = rm.room_id " +
            "LEFT JOIN bills b ON r.reservation_id = b.reservation_id " +
            "LEFT JOIN users u ON r.created_by = u.user_id " +
            "WHERE r.reservation_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEnriched(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching enriched reservation by id", e);
        }
    }

    @Override
    public ReservationListDTO findEnrichedByNumber(String reservationNumber) {
        String sql = "SELECT r.reservation_id, r.reservation_number, r.guest_id, g.guest_name, g.contact_number, " +
            "r.room_id, rm.room_number, rm.room_type, rm.price_per_night, " +
            "r.check_in_date, r.check_out_date, r.status, r.special_requests, " +
            "b.bill_id, b.total_amount, b.status AS bill_status, " +
            "r.created_at, u.username AS created_by_username " +
            "FROM reservations r " +
            "LEFT JOIN guests g ON r.guest_id = g.guest_id " +
            "LEFT JOIN rooms rm ON r.room_id = rm.room_id " +
            "LEFT JOIN bills b ON r.reservation_id = b.reservation_id " +
            "LEFT JOIN users u ON r.created_by = u.user_id " +
            "WHERE r.reservation_number = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEnriched(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching enriched reservation by number", e);
        }
    }

    private ReservationListDTO mapEnriched(ResultSet rs) throws SQLException {
        ReservationListDTO dto = new ReservationListDTO();
        dto.setReservationId(rs.getInt("reservation_id"));
        dto.setReservationNumber(rs.getString("reservation_number"));
        dto.setGuestId(rs.getInt("guest_id"));
        dto.setGuestName(rs.getString("guest_name"));
        dto.setContactNumber(rs.getString("contact_number"));
        dto.setRoomId(rs.getInt("room_id"));
        dto.setRoomNumber(rs.getString("room_number"));
        String roomType = rs.getString("room_type");
        if (roomType != null) dto.setRoomType(RoomType.valueOf(roomType));
        dto.setPricePerNight(rs.getBigDecimal("price_per_night"));
        Date checkIn = rs.getDate("check_in_date");
        Date checkOut = rs.getDate("check_out_date");
        if (checkIn != null) dto.setCheckInDate(checkIn.toLocalDate());
        if (checkOut != null) dto.setCheckOutDate(checkOut.toLocalDate());
        if (checkIn != null && checkOut != null) {
            dto.setNights(ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate()));
        }
        String status = rs.getString("status");
        if (status != null) dto.setStatus(ReservationStatus.valueOf(status));
        dto.setSpecialRequests(rs.getString("special_requests"));
        int billId = rs.getInt("bill_id");
        if (!rs.wasNull()) dto.setBillId(billId);
        dto.setTotalAmount(rs.getBigDecimal("total_amount"));
        String billStatus = rs.getString("bill_status");
        if (billStatus != null) dto.setBillStatus(BillStatus.valueOf(billStatus));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) dto.setCreatedAt(createdAt.toLocalDateTime());
        dto.setCreatedByUsername(rs.getString("created_by_username"));
        return dto;
    }

    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setReservationId(rs.getInt("reservation_id"));
        r.setReservationNumber(rs.getString("reservation_number"));
        r.setGuestId(rs.getInt("guest_id"));
        r.setRoomId(rs.getInt("room_id"));
        r.setCreatedBy(rs.getInt("created_by"));
        r.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        r.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
        String status = rs.getString("status");
        if (status != null) r.setStatus(ReservationStatus.valueOf(status));
        r.setSpecialRequests(rs.getString("special_requests"));
        return r;
    }
}
