package com.oceanview.reservation.dao;

import com.oceanview.reservation.model.AuditLog;

public interface AuditDAO {
    void log(AuditLog log);
    java.util.List<AuditLog> findAll();
}
