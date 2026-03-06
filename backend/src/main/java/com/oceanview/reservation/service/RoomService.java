package com.oceanview.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oceanview.reservation.dao.AuditDAO;
import com.oceanview.reservation.dao.DAOFactory;
import com.oceanview.reservation.dao.RoomDAO;
import com.oceanview.reservation.model.AuditLog;
import com.oceanview.reservation.model.Room;
import com.oceanview.reservation.model.enums.RoomStatus;
import com.oceanview.reservation.model.enums.RoomType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RoomService {

    private final RoomDAO roomDAO = DAOFactory.getRoomDAO();
    private final AuditDAO auditDAO = DAOFactory.getAuditDAO();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public List<Room> getAllRooms() {
        return roomDAO.findAll();
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        return roomDAO.findAvailable(checkIn, checkOut, type);
    }

    public Room getRoomById(int id) {
        return roomDAO.findById(id);
    }

    public boolean updateRoomStatus(int roomId, RoomStatus newStatus, Integer userId, String ipAddress) {
        Room oldRoom = roomDAO.findById(roomId);
        if (oldRoom == null) return false;

        boolean updated = roomDAO.updateStatus(roomId, newStatus);
        if (updated) {
            logAudit(userId, "UPDATE", "ROOM", roomId, "{\"status\": \"" + oldRoom.getStatus() + "\"}", "{\"status\": \"" + newStatus + "\"}", ipAddress);
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
