package com.oceanview.reservation.dao.impl;

import com.oceanview.reservation.dao.BillDAO;
import com.oceanview.reservation.dto.BillDTO;
import com.oceanview.reservation.model.Bill;
import com.oceanview.reservation.model.enums.BillStatus;
import com.oceanview.reservation.model.enums.PaymentMethod;
import com.oceanview.reservation.model.enums.RoomType;
import com.oceanview.reservation.util.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.time.temporal.ChronoUnit;

public class BillDAOImpl implements BillDAO {

    @Override
    public int create(Bill b) {
        String sql = "INSERT INTO bills (reservation_id, room_charges, total_amount, status, payment_method, issued_date, paid_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getReservationId());
            ps.setBigDecimal(2, b.getRoomCharges());
            ps.setBigDecimal(3, b.getTotalAmount());
            ps.setString(4, b.getStatus().name());
            if (b.getPaymentMethod() != null) ps.setString(5, b.getPaymentMethod().name()); else ps.setNull(5, Types.VARCHAR);
            if (b.getIssuedDate() != null) ps.setTimestamp(6, Timestamp.valueOf(b.getIssuedDate())); else ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            if (b.getPaidDate() != null) ps.setTimestamp(7, Timestamp.valueOf(b.getPaidDate())); else ps.setNull(7, Types.TIMESTAMP);
            int affected = ps.executeUpdate();
            if (affected == 0) throw new RuntimeException("Creating bill failed, no rows affected.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new RuntimeException("Creating bill failed, no ID obtained.");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating bill", e);
        }
    }

    @Override
    public Bill findByReservationId(int reservationId) {
        String sql = "SELECT * FROM bills WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bill by reservation id", e);
        }
    }

    @Override
    public BillDTO findEnrichedByReservationId(int reservationId) {
        String sql = "SELECT b.bill_id, b.reservation_id, r.reservation_number, " +
            "g.guest_name, g.contact_number, " +
            "rm.room_number, rm.room_type, rm.price_per_night, " +
            "r.check_in_date, r.check_out_date, " +
            "b.room_charges, b.total_amount, b.status, b.payment_method, " +
            "b.issued_date, b.paid_date " +
            "FROM bills b " +
            "JOIN reservations r ON b.reservation_id = r.reservation_id " +
            "LEFT JOIN guests g ON r.guest_id = g.guest_id " +
            "LEFT JOIN rooms rm ON r.room_id = rm.room_id " +
            "WHERE b.reservation_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEnriched(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching enriched bill by reservation id", e);
        }
    }

    private BillDTO mapEnriched(ResultSet rs) throws SQLException {
        BillDTO dto = new BillDTO();
        dto.setBillId(rs.getInt("bill_id"));
        dto.setReservationId(rs.getInt("reservation_id"));
        dto.setReservationNumber(rs.getString("reservation_number"));
        dto.setGuestName(rs.getString("guest_name"));
        dto.setContactNumber(rs.getString("contact_number"));
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
        dto.setRoomCharges(rs.getBigDecimal("room_charges"));
        dto.setTotalAmount(rs.getBigDecimal("total_amount"));
        String status = rs.getString("status");
        if (status != null) dto.setStatus(BillStatus.valueOf(status));
        String method = rs.getString("payment_method");
        if (method != null) dto.setPaymentMethod(PaymentMethod.valueOf(method));
        Timestamp issued = rs.getTimestamp("issued_date");
        if (issued != null) dto.setIssuedDate(issued.toLocalDateTime());
        Timestamp paid = rs.getTimestamp("paid_date");
        if (paid != null) dto.setPaidDate(paid.toLocalDateTime());
        return dto;
    }

    @Override
    public boolean updateStatus(int billId, BillStatus status, PaymentMethod method) {
        String sql = "UPDATE bills SET status = ?, payment_method = ?, paid_date = ?, updated_at = CURRENT_TIMESTAMP WHERE bill_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            if (method != null) ps.setString(2, method.name()); else ps.setNull(2, Types.VARCHAR);
            if (status == BillStatus.PAID) ps.setTimestamp(3, new Timestamp(System.currentTimeMillis())); else ps.setNull(3, Types.TIMESTAMP);
            ps.setInt(4, billId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating bill status", e);
        }
    }

    @Override
    public java.math.BigDecimal getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM bills WHERE status = 'PAID'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal revenue = rs.getBigDecimal(1);
                return revenue != null ? revenue : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching total revenue", e);
        }
    }

    @Override
    public java.util.List<Bill> findAll() {
        String sql = "SELECT * FROM bills";
        java.util.List<Bill> list = new java.util.ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all bills", e);
        }
    }

    @Override
    public java.util.List<java.util.Map<String, Object>> getRevenueReportData(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        String sql = "SELECT b.bill_id, b.total_amount, b.status, b.paid_date, " +
            "r.reservation_number, rm.room_type, g.guest_name " +
            "FROM bills b " +
            "JOIN reservations r ON b.reservation_id = r.reservation_id " +
            "LEFT JOIN rooms rm ON r.room_id = rm.room_id " +
            "LEFT JOIN guests g ON r.guest_id = g.guest_id " +
            "WHERE b.issued_date >= ? AND b.issued_date < ? " +
            "ORDER BY b.paid_date DESC";
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();
                    row.put("billId", rs.getInt("bill_id"));
                    row.put("totalAmount", rs.getBigDecimal("total_amount"));
                    row.put("status", rs.getString("status"));
                    Timestamp paid = rs.getTimestamp("paid_date");
                    row.put("paidDate", paid != null ? paid.toLocalDateTime() : null);
                    row.put("reservationNumber", rs.getString("reservation_number"));
                    row.put("roomType", rs.getString("room_type"));
                    row.put("guestName", rs.getString("guest_name"));
                    list.add(row);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching revenue report data", e);
        }
    }

    private Bill map(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setReservationId(rs.getInt("reservation_id"));
        b.setRoomCharges(rs.getBigDecimal("room_charges"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        String status = rs.getString("status");
        if (status != null) b.setStatus(BillStatus.valueOf(status));
        String method = rs.getString("payment_method");
        if (method != null) b.setPaymentMethod(PaymentMethod.valueOf(method));
        Timestamp issued = rs.getTimestamp("issued_date");
        if (issued != null) b.setIssuedDate(issued.toLocalDateTime());
        Timestamp paid = rs.getTimestamp("paid_date");
        if (paid != null) b.setPaidDate(paid.toLocalDateTime());
        Timestamp upd = rs.getTimestamp("updated_at");
        if (upd != null) b.setUpdatedAt(upd.toLocalDateTime());
        return b;
    }
}
