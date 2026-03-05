package com.oceanview.reservation.dao.impl;

import com.oceanview.reservation.dao.ReservationDAO;
import com.oceanview.reservation.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
