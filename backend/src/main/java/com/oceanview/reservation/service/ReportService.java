package com.oceanview.reservation.service;

import com.oceanview.reservation.dao.*;
import com.oceanview.reservation.dto.DashboardStats;
import com.oceanview.reservation.model.AuditLog;
import com.oceanview.reservation.model.Reservation;
import com.oceanview.reservation.model.Guest;
import com.oceanview.reservation.model.enums.RoomStatus;
import com.oceanview.reservation.model.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {

    private final GuestDAO guestDAO = DAOFactory.getGuestDAO();
    private final ReservationDAO reservationDAO = DAOFactory.getReservationDAO();
    private final RoomDAO roomDAO = DAOFactory.getRoomDAO();
    private final BillDAO billDAO = DAOFactory.getBillDAO();
    private final AuditDAO auditDAO = DAOFactory.getAuditDAO();

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();

        List<Reservation> allReservations = reservationDAO.findAll();

        // Today's check-ins
        long todayCheckIns = allReservations.stream()
                .filter(r -> r.getCheckInDate() != null && r.getCheckInDate().equals(today))
                .count();
        stats.setTodayCheckIns((int) todayCheckIns);

        // Today's check-outs
        long todayCheckOuts = allReservations.stream()
                .filter(r -> r.getCheckOutDate() != null && r.getCheckOutDate().equals(today))
                .count();
        stats.setTodayCheckOuts((int) todayCheckOuts);

        // Current occupancy (active reservations)
        int currentOccupancy = reservationDAO.getActiveReservationCount();
        stats.setCurrentOccupancy(currentOccupancy);

        // Total rooms
        int totalRooms = roomDAO.findAll().size();
        stats.setTotalRooms(totalRooms);

        // Monthly revenue (bills from current month)
        BigDecimal monthlyRevenue = billDAO.findAll().stream()
                .filter(bill -> {
                    if (bill.getIssuedDate() != null) {
                        YearMonth billMonth = YearMonth.from(bill.getIssuedDate());
                        return billMonth.equals(currentMonth);
                    }
                    return false;
                })
                .map(bill -> bill.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setMonthlyRevenue(monthlyRevenue);

        // Total guests this month (unique guests with reservations this month)
        long guestsThisMonth = allReservations.stream()
                .filter(r -> {
                    if (r.getCheckInDate() != null) {
                        YearMonth resMonth = YearMonth.from(r.getCheckInDate());
                        return resMonth.equals(currentMonth);
                    }
                    return false;
                })
                .map(r -> r.getGuestId())
                .distinct()
                .count();
        stats.setTotalGuestsThisMonth((int) guestsThisMonth);

        return stats;
    }

    public Map<String, Object> getRevenueReport(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> rows = billDAO.getRevenueReportData(startDate, endDate);

        int totalBillsCount = rows.size();
        List<Map<String, Object>> paidRows = rows.stream()
                .filter(r -> "PAID".equals(r.get("status")))
                .collect(Collectors.toList());
        int paidBillsCount = paidRows.size();

        BigDecimal totalRevenue = paidRows.stream()
                .map(r -> (BigDecimal) r.get("totalAmount"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> revenueByRoomType = paidRows.stream()
                .filter(r -> r.get("roomType") != null)
                .collect(Collectors.groupingBy(
                        r -> (String) r.get("roomType"),
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, r -> (BigDecimal) r.get("totalAmount"), BigDecimal::add)
                ));

        List<Map<String, Object>> recentPayments = paidRows.stream()
                .filter(r -> r.get("paidDate") != null)
                .limit(10)
                .map(r -> {
                    Map<String, Object> payment = new LinkedHashMap<>();
                    payment.put("guestName", r.get("guestName"));
                    payment.put("reservationNumber", r.get("reservationNumber"));
                    payment.put("paidDate", r.get("paidDate"));
                    payment.put("amount", r.get("totalAmount"));
                    return payment;
                })
                .collect(Collectors.toList());

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalRevenue", totalRevenue);
        report.put("paidBillsCount", paidBillsCount);
        report.put("totalBillsCount", totalBillsCount);
        report.put("revenueByRoomType", revenueByRoomType);
        report.put("recentPayments", recentPayments);
        return report;
    }

    public List<AuditLog> getAuditLogs() {
        return auditDAO.findAll();
    }
}
