package com.oceanview.reservation.dao;

import com.oceanview.reservation.dto.BillDTO;
import com.oceanview.reservation.model.Bill;
import com.oceanview.reservation.model.enums.BillStatus;
import com.oceanview.reservation.model.enums.PaymentMethod;
import java.util.List;

public interface BillDAO {
    int create(Bill bill);
    Bill findByReservationId(int reservationId);
    BillDTO findEnrichedByReservationId(int reservationId);
    boolean updateStatus(int billId, BillStatus status, PaymentMethod method);
    java.math.BigDecimal getTotalRevenue();
    List<Bill> findAll();
    List<java.util.Map<String, Object>> getRevenueReportData(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
