package com.oceanview.reservation.dao;

import com.oceanview.reservation.model.Bill;
import com.oceanview.reservation.model.enums.BillStatus;
import com.oceanview.reservation.model.enums.PaymentMethod;

public interface BillDAO {
    int create(Bill bill);
    Bill findByReservationId(int reservationId);
    boolean updateStatus(int billId, BillStatus status, PaymentMethod method);
    java.math.BigDecimal getTotalRevenue();
}
