package com.oceanview.reservation.dao.impl;

import com.oceanview.reservation.dao.RoomDAO;
import com.oceanview.reservation.model.Room;
import com.oceanview.reservation.model.enums.RoomStatus;
import com.oceanview.reservation.model.enums.RoomType;
import com.oceanview.reservation.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAOImpl implements RoomDAO {

    @Override
    public List<Room> findAll() {
        String sql = "SELECT * FROM rooms ORDER BY room_id";
        List<Room> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching rooms", e);
        }
    }

    @Override
    public List<Room> findAvailable(java.time.LocalDate checkIn, java.time.LocalDate checkOut, RoomType type) {
        // A room is available if: status=AVAILABLE and it has no overlapping reservations with active statuses in the given range
        String sql = "SELECT r.* FROM rooms r " +
                     "WHERE r.status = 'AVAILABLE' " +
                     (type != null ? "AND r.room_type = ? " : "") +
                     "AND NOT EXISTS (" +
                     "  SELECT 1 FROM reservations rr WHERE rr.room_id = r.room_id " +
                     "    AND rr.status IN ('PENDING','CONFIRMED','CHECKED_IN') " +
                     "    AND NOT (rr.check_out_date <= ? OR rr.check_in_date >= ?)" +
                     ") " +
                     "ORDER BY r.room_id";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            if (type != null) {
                ps.setString(idx++, type.name());
            }
            ps.setDate(idx++, Date.valueOf(checkIn));
            ps.setDate(idx, Date.valueOf(checkOut));
            List<Room> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching available rooms", e);
        }
    }

    @Override
    public boolean updateStatus(int roomId, RoomStatus status) {
        String sql = "UPDATE rooms SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating room status", e);
        }
    }

    @Override
    public Room findById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching room by id", e);
        }
    }

    private Room map(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setRoomId(rs.getInt("room_id"));
        r.setRoomNumber(rs.getString("room_number"));
        String type = rs.getString("room_type");
        if (type != null) r.setRoomType(RoomType.valueOf(type));
        r.setPricePerNight(rs.getBigDecimal("price_per_night"));
        String status = rs.getString("status");
        if (status != null) r.setStatus(RoomStatus.valueOf(status));
        r.setFloorNumber(rs.getInt("floor_number"));
        r.setDescription(rs.getString("description"));
        return r;
    }
}
