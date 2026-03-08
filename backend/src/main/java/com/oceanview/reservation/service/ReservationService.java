package com.oceanview.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oceanview.reservation.dao.AuditDAO;
import com.oceanview.reservation.dao.DAOFactory;
import com.oceanview.reservation.dao.ReservationDAO;
import com.oceanview.reservation.dao.RoomDAO;
import com.oceanview.reservation.dto.ReservationListDTO;
import com.oceanview.reservation.model.AuditLog;
import com.oceanview.reservation.model.Reservation;
import com.oceanview.reservation.model.enums.ReservationStatus;
import com.oceanview.reservation.model.enums.RoomStatus;
import com.oceanview.reservation.util.ReservationNumberGenerator;

import java.time.LocalDateTime;

public class ReservationService {

    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private final AuditDAO auditDAO;
    private final BillingService billingService;
    private final ObjectMapper objectMapper;

    public ReservationService() {
        this(DAOFactory.getReservationDAO(), DAOFactory.getRoomDAO(), DAOFactory.getAuditDAO(), new BillingService());
    }

    public ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO, AuditDAO auditDAO, BillingService billingService) {
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
        this.auditDAO = auditDAO;
        this.billingService = billingService;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public Reservation createReservation(Reservation reservation, Integer userId, String ipAddress) {
        // 1. Check availability
        if (reservationDAO.existsRoomConflict(reservation.getRoomId(), reservation.getCheckInDate(), reservation.getCheckOutDate(), null)) {
            throw new RuntimeException("Room is already booked for these dates");
        }

        // 2. Set defaults
        reservation.setReservationNumber(ReservationNumberGenerator.getInstance().generate());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedBy(userId != null ? userId : 0);
        
        // 3. Save reservation
        int id = reservationDAO.create(reservation);
        reservation.setReservationId(id);

        // 4. Generate bill
        billingService.generateBill(reservation);

        // 5. Log audit
        logAudit(userId, "CREATE", "RESERVATION", id, null, toJson(reservation), ipAddress);

        return reservation;
    }

    public Reservation getReservationById(int id) {
        return reservationDAO.findById(id);
    }

    public Reservation getReservationByNumber(String number) {
        return reservationDAO.findByNumber(number);
    }

    public boolean updateReservationStatus(int id, ReservationStatus newStatus, Integer userId, String ipAddress) {
        Reservation oldReservation = reservationDAO.findById(id);
        if (oldReservation == null) return false;

        boolean updated = reservationDAO.updateStatus(id, newStatus);
        if (updated) {
            // Update room status if necessary (e.g., when checking in)
            if (newStatus == ReservationStatus.CHECKED_IN) {
                roomDAO.updateStatus(oldReservation.getRoomId(), RoomStatus.OCCUPIED);
            } else if (newStatus == ReservationStatus.CHECKED_OUT || newStatus == ReservationStatus.CANCELLED) {
                roomDAO.updateStatus(oldReservation.getRoomId(), RoomStatus.AVAILABLE);
            }

            logAudit(userId, "UPDATE", "RESERVATION", id, "{\"status\": \"" + oldReservation.getStatus() + "\"}", "{\"status\": \"" + newStatus + "\"}", ipAddress);
        }
        return updated;
    }

    public java.util.List<Reservation> listReservations(int offset, int limit, ReservationStatus status) {
        return reservationDAO.findWithPagination(offset, limit, status);
    }

    public int countReservations(ReservationStatus status) {
        return reservationDAO.countReservations(status);
    }

    public ReservationListDTO getEnrichedReservationById(int id) {
        return reservationDAO.findEnrichedById(id);
    }

    public ReservationListDTO getEnrichedReservationByNumber(String number) {
        return reservationDAO.findEnrichedByNumber(number);
    }

    public java.util.List<ReservationListDTO> listEnrichedReservations(int offset, int limit, ReservationStatus status, String search, Integer guestId) {
        return reservationDAO.findEnrichedWithPagination(offset, limit, status, search, guestId);
    }

    public int countEnrichedReservations(ReservationStatus status, String search, Integer guestId) {
        return reservationDAO.countEnrichedReservations(status, search, guestId);
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
