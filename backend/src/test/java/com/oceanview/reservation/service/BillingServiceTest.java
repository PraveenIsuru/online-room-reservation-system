package com.oceanview.reservation.service;

import com.oceanview.reservation.dao.BillDAO;
import com.oceanview.reservation.dao.RoomDAO;
import com.oceanview.reservation.dao.AuditDAO;
import com.oceanview.reservation.model.Bill;
import com.oceanview.reservation.model.Reservation;
import com.oceanview.reservation.model.Room;
import com.oceanview.reservation.model.enums.BillStatus;
import com.oceanview.reservation.model.enums.PaymentMethod;
import com.oceanview.reservation.service.billing.BillingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillingServiceTest {

    @Mock
    private BillDAO billDAO;
    @Mock
    private RoomDAO roomDAO;
    @Mock
    private AuditDAO auditDAO;
    @Mock
    private BillingStrategy billingStrategy;

    private BillingService billingService;

    @BeforeEach
    void setUp() {
        billingService = new BillingService(billDAO, roomDAO, auditDAO, billingStrategy);
    }

    @Test
    void generateBill_Success() {
        Reservation res = new Reservation();
        res.setReservationId(1);
        res.setRoomId(101);

        Room room = new Room();
        room.setRoomId(101);

        BigDecimal roomCharges = new BigDecimal("200.00");
        BigDecimal totalAmount = new BigDecimal("210.00");

        when(roomDAO.findById(101)).thenReturn(room);
        when(billingStrategy.calculateRoomCharges(res, room)).thenReturn(roomCharges);
        when(billingStrategy.calculateTotalAmount(roomCharges)).thenReturn(totalAmount);
        when(billDAO.create(any(Bill.class))).thenReturn(1);

        Bill bill = billingService.generateBill(res);

        assertNotNull(bill);
        assertEquals(1, bill.getBillId());
        assertEquals(roomCharges, bill.getRoomCharges());
        assertEquals(totalAmount, bill.getTotalAmount());
        assertEquals(BillStatus.PENDING, bill.getStatus());
    }

    @Test
    void payBill_Success() {
        int billId = 1;
        PaymentMethod method = PaymentMethod.CARD;

        when(billDAO.updateStatus(billId, BillStatus.PAID, method)).thenReturn(true);

        boolean result = billingService.payBill(billId, method, 1, "127.0.0.1");

        assertTrue(result);
        verify(auditDAO).log(any());
    }
}
