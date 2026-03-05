package com.oceanview.reservation.dao.impl;

import com.oceanview.reservation.dao.GuestDAO;
import com.oceanview.reservation.model.Guest;
import com.oceanview.reservation.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GuestDAOImpl implements GuestDAO {

    @Override
    public List<Guest> findAll() {
        String sql = "SELECT * FROM guests ORDER BY guest_id";
        List<Guest> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching guests", e);
        }
    }

    @Override
    public Guest findById(int guestId) {
        String sql = "SELECT * FROM guests WHERE guest_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching guest by id", e);
        }
    }

    @Override
    public Guest findByContact(String contactOrEmail) {
        String sql = "SELECT * FROM guests WHERE contact_number = ? OR email = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contactOrEmail);
            ps.setString(2, contactOrEmail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching guest by contact/email", e);
        }
    }

    @Override
    public int create(Guest g) {
        String sql = "INSERT INTO guests (guest_name, address, contact_number, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, g.getGuestName());
            ps.setString(2, g.getAddress());
            ps.setString(3, g.getContactNumber());
            ps.setString(4, g.getEmail());
            int affected = ps.executeUpdate();
            if (affected == 0) throw new RuntimeException("Creating guest failed, no rows affected.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new RuntimeException("Creating guest failed, no ID obtained.");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating guest", e);
        }
    }

    @Override
    public boolean update(Guest g) {
        String sql = "UPDATE guests SET guest_name = ?, address = ?, contact_number = ?, email = ?, updated_at = CURRENT_TIMESTAMP WHERE guest_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, g.getGuestName());
            ps.setString(2, g.getAddress());
            ps.setString(3, g.getContactNumber());
            ps.setString(4, g.getEmail());
            ps.setInt(5, g.getGuestId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating guest", e);
        }
    }

    private Guest map(ResultSet rs) throws SQLException {
        Guest g = new Guest();
        g.setGuestId(rs.getInt("guest_id"));
        g.setGuestName(rs.getString("guest_name"));
        g.setAddress(rs.getString("address"));
        g.setContactNumber(rs.getString("contact_number"));
        g.setEmail(rs.getString("email"));
        Timestamp cAt = rs.getTimestamp("created_at");
        if (cAt != null) g.setCreatedAt(cAt.toLocalDateTime());
        Timestamp uAt = rs.getTimestamp("updated_at");
        if (uAt != null) g.setUpdatedAt(uAt.toLocalDateTime());
        return g;
    }
}
