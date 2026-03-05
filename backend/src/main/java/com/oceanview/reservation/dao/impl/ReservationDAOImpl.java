package com.oceanview.reservation.dao.impl;

import com.oceanview.reservation.dao.ReservationDAO;
import com.oceanview.reservation.model.Reservation;
import com.oceanview.reservation.model.enums.ReservationStatus;
import com.oceanview.reservation.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
