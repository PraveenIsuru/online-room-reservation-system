package com.oceanview.reservation.service;

import com.oceanview.reservation.dao.ReservationDAO;
import com.oceanview.reservation.dao.RoomDAO;
import com.oceanview.reservation.dao.AuditDAO;
import com.oceanview.reservation.model.Reservation;
import com.oceanview.reservation.model.enums.ReservationStatus;
import com.oceanview.reservation.model.enums.RoomStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationDAO reservationDAO;
    @Mock
    private RoomDAO roomDAO;
    @Mock
    private AuditDAO auditDAO;
    @Mock
    private BillingService billingService;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(reservationDAO, roomDAO, auditDAO, billingService);
    }

    @Test
    void createReservation_Success() {
        Reservation res = new Reservation();
        res.setRoomId(101);
        res.setCheckInDate(LocalDate.now());
        res.setCheckOutDate(LocalDate.now().plusDays(2));

        when(reservationDAO.existsRoomConflict(anyInt(), any(), any(), any())).thenReturn(false);
        when(reservationDAO.create(any(Reservation.class))).thenReturn(1);

        Reservation created = reservationService.createReservation(res, 1, "127.0.0.1");

        assertNotNull(created);
        assertEquals(1, created.getReservationId());
        assertEquals(ReservationStatus.PENDING, created.getStatus());
        verify(billingService).generateBill(created);
        verify(auditDAO).log(any());
    }

    @Test
    void createReservation_Conflict() {
        Reservation res = new Reservation();
        res.setRoomId(101);
        res.setCheckInDate(LocalDate.now());
        res.setCheckOutDate(LocalDate.now().plusDays(2));

        when(reservationDAO.existsRoomConflict(anyInt(), any(), any(), any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> reservationService.createReservation(res, 1, "127.0.0.1"));
    }

    @Test
    void updateReservationStatus_CheckIn() {
        Reservation res = new Reservation();
        res.setReservationId(1);
        res.setRoomId(101);
        res.setStatus(ReservationStatus.PENDING);

        when(reservationDAO.findById(1)).thenReturn(res);
        when(reservationDAO.updateStatus(1, ReservationStatus.CHECKED_IN)).thenReturn(true);

        boolean updated = reservationService.updateReservationStatus(1, ReservationStatus.CHECKED_IN, 1, "127.0.0.1");

        assertTrue(updated);
        verify(roomDAO).updateStatus(101, RoomStatus.OCCUPIED);
        verify(auditDAO).log(any());
    }

    @Test
    void updateReservationStatus_CheckOut() {
        Reservation res = new Reservation();
        res.setReservationId(1);
        res.setRoomId(101);
        res.setStatus(ReservationStatus.CHECKED_IN);

        when(reservationDAO.findById(1)).thenReturn(res);
        when(reservationDAO.updateStatus(1, ReservationStatus.CHECKED_OUT)).thenReturn(true);

        boolean updated = reservationService.updateReservationStatus(1, ReservationStatus.CHECKED_OUT, 1, "127.0.0.1");

        assertTrue(updated);
        verify(roomDAO).updateStatus(101, RoomStatus.AVAILABLE);
        verify(auditDAO).log(any());
    }
}
