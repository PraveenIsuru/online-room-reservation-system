package com.oceanview.reservation.service;

import com.oceanview.reservation.dao.AuditDAO;
import com.oceanview.reservation.dao.DAOFactory;
import com.oceanview.reservation.dao.GuestDAO;
import com.oceanview.reservation.model.AuditLog;
import com.oceanview.reservation.model.Guest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.util.List;

public class GuestService {

    private final GuestDAO guestDAO = DAOFactory.getGuestDAO();
    private final AuditDAO auditDAO = DAOFactory.getAuditDAO();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public List<Guest> getAllGuests() {
        return guestDAO.findAll();
    }

    public Guest getGuestById(int id) {
        return guestDAO.findById(id);
    }

    public Guest createGuest(Guest guest, Integer userId, String ipAddress) {
        int id = guestDAO.create(guest);
        guest.setGuestId(id);
        
        logAudit(userId, "CREATE", "GUEST", id, null, toJson(guest), ipAddress);
        
        return guest;
    }

    public boolean updateGuest(Guest guest, Integer userId, String ipAddress) {
        Guest oldGuest = guestDAO.findById(guest.getGuestId());
        if (oldGuest == null) return false;

        boolean updated = guestDAO.update(guest);
        if (updated) {
            logAudit(userId, "UPDATE", "GUEST", guest.getGuestId(), toJson(oldGuest), toJson(guest), ipAddress);
        }
        return updated;
    }

    private void logAudit(Integer userId, String action, String entityType, Integer entityId, String oldValue, String newValue, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setActionTime(LocalDateTime.now());
        log.setIpAddress(ipAddress);
        auditDAO.log(log);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "Error serializing to JSON";
        }
    }
}
