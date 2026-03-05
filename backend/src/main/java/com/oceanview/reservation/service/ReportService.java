package com.oceanview.reservation.service;

import com.oceanview.reservation.dao.*;
import com.oceanview.reservation.dto.DashboardStats;
import com.oceanview.reservation.model.AuditLog;
import com.oceanview.reservation.model.enums.RoomStatus;

import java.util.List;

public class ReportService {

    private final GuestDAO guestDAO = DAOFactory.getGuestDAO();
    private final ReservationDAO reservationDAO = DAOFactory.getReservationDAO();
    private final RoomDAO roomDAO = DAOFactory.getRoomDAO();
    private final BillDAO billDAO = DAOFactory.getBillDAO();
    private final AuditDAO auditDAO = DAOFactory.getAuditDAO();

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        stats.setTotalGuests(guestDAO.findAll().size());
        stats.setActiveReservations(reservationDAO.getActiveReservationCount());
        stats.setAvailableRooms(roomDAO.findAll().stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                .count());
        stats.setTotalRevenue(billDAO.getTotalRevenue());

        return stats;
    }

    public List<AuditLog> getAuditLogs() {
        return auditDAO.findAll();
    }
}
