package com.oceanview.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oceanview.reservation.dao.AuditDAO;
import com.oceanview.reservation.dao.BillDAO;
import com.oceanview.reservation.dao.DAOFactory;
import com.oceanview.reservation.dao.RoomDAO;
import com.oceanview.reservation.model.AuditLog;
import com.oceanview.reservation.model.Bill;
import com.oceanview.reservation.model.Reservation;
import com.oceanview.reservation.model.Room;
import com.oceanview.reservation.model.enums.BillStatus;
import com.oceanview.reservation.model.enums.PaymentMethod;
import com.oceanview.reservation.service.billing.BillingStrategy;
import com.oceanview.reservation.service.billing.StandardBillingStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillingService {

    private final BillDAO billDAO = DAOFactory.getBillDAO();
    private final RoomDAO roomDAO = DAOFactory.getRoomDAO();
    private final AuditDAO auditDAO = DAOFactory.getAuditDAO();
    private final BillingStrategy billingStrategy = new StandardBillingStrategy();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public Bill generateBill(Reservation reservation) {
        Room room = roomDAO.findById(reservation.getRoomId());
        if (room == null) return null;

        BigDecimal roomCharges = billingStrategy.calculateRoomCharges(reservation, room);
        BigDecimal totalAmount = billingStrategy.calculateTotalAmount(roomCharges);

        Bill bill = new Bill();
        bill.setReservationId(reservation.getReservationId());
        bill.setRoomCharges(roomCharges);
        bill.setTotalAmount(totalAmount);
        bill.setStatus(BillStatus.PENDING);
        bill.setIssuedDate(LocalDateTime.now());

        int billId = billDAO.create(bill);
        bill.setBillId(billId);
        
        return bill;
    }

    public Bill getBillByReservationId(int reservationId) {
        return billDAO.findByReservationId(reservationId);
    }

    public boolean payBill(int billId, PaymentMethod paymentMethod, Integer userId, String ipAddress) {
        // Here we could add more logic for actual payment processing
        boolean updated = billDAO.updateStatus(billId, BillStatus.PAID, paymentMethod);
        if (updated) {
            logAudit(userId, "UPDATE", "BILL", billId, "Status: PENDING", "Status: PAID, Method: " + paymentMethod, ipAddress);
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
}
