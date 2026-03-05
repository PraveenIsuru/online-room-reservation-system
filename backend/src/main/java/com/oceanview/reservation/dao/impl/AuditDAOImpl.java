package com.oceanview.reservation.dao.impl;

import com.oceanview.reservation.dao.AuditDAO;
import com.oceanview.reservation.model.AuditLog;
import com.oceanview.reservation.util.DatabaseConnection;
import java.sql.*;

public class AuditDAOImpl implements AuditDAO {
    @Override
    public void log(AuditLog log) {
        String sql = "INSERT INTO audit_log (user_id, action, entity_type, entity_id, old_value, new_value, action_time, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (log.getUserId() != null) ps.setInt(1, log.getUserId()); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, log.getAction());
            ps.setString(3, log.getEntityType());
            if (log.getEntityId() != null) ps.setInt(4, log.getEntityId()); else ps.setNull(4, Types.INTEGER);
            ps.setString(5, log.getOldValue());
            ps.setString(6, log.getNewValue());
            if (log.getActionTime() != null) ps.setTimestamp(7, Timestamp.valueOf(log.getActionTime())); else ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            ps.setString(8, log.getIpAddress());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting audit log", e);
        }
    }
}
