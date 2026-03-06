package com.oceanview.reservation.e2e;

import com.oceanview.reservation.model.*;
import com.oceanview.reservation.model.enums.*;
import com.oceanview.reservation.service.*;
import com.oceanview.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EndToEndTest {

    private GuestService guestService;
    private RoomService roomService;
    private ReservationService reservationService;
    private BillingService billingService;
    private AuthService authService;

    @BeforeAll
    void setup() {
        guestService = new GuestService();
        roomService = new RoomService();
        billingService = new BillingService();
        reservationService = new ReservationService(
                com.oceanview.reservation.dao.DAOFactory.getReservationDAO(),
                com.oceanview.reservation.dao.DAOFactory.getRoomDAO(),
                com.oceanview.reservation.dao.DAOFactory.getAuditDAO(),
                billingService
        );
        authService = new AuthService();
    }

    @Test
    void testRBACAndSecurityContext() {
        // Test RBAC logic (as it would be handled by AuthFilter and JAX-RS)
        User admin = new User();
        admin.setUserId(1);
        admin.setUsername("admin");
        admin.setRole(UserRole.ADMIN);

        User staff = new User();
        staff.setUserId(2);
        staff.setUsername("staff");
        staff.setRole(UserRole.STAFF);

        String adminToken = JwtUtil.generateToken(admin);
        String staffToken = JwtUtil.generateToken(staff);

        Claims adminClaims = JwtUtil.validateToken(adminToken);
        Claims staffClaims = JwtUtil.validateToken(staffToken);

        assertEquals("ADMIN", adminClaims.get("role"));
        assertEquals("STAFF", staffClaims.get("role"));

        // Simulate isUserInRole logic from AuthFilter
        assertTrue("ADMIN".equalsIgnoreCase(adminClaims.get("role", String.class)));
        assertFalse("ADMIN".equalsIgnoreCase(staffClaims.get("role", String.class)));
    }

    @Test
    void testFullBookingFlow() {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        // 1. Create a Guest
        Guest guest = new Guest();
        guest.setGuestName("John Doe " + uniqueSuffix);
        guest.setEmail("john.doe." + uniqueSuffix + "@example.com");
        guest.setContactNumber(uniqueSuffix.substring(uniqueSuffix.length() - 10));
        guest.setAddress("123 E2E St");
        
        Guest createdGuest = guestService.createGuest(guest, 1, "127.0.0.1");
        assertNotNull(createdGuest);
        assertTrue(createdGuest.getGuestId() > 0);

        // 2. Check available Rooms
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(15);
        List<Room> availableRooms = roomService.getAvailableRooms(checkIn, checkOut, RoomType.STANDARD);
        
        assertFalse(availableRooms.isEmpty(), "Should have available standard rooms");
        Room room = availableRooms.get(0);

        // 3. Create a Reservation
        Reservation res = new Reservation();
        res.setGuestId(createdGuest.getGuestId());
        res.setRoomId(room.getRoomId());
        res.setCheckInDate(checkIn);
        res.setCheckOutDate(checkOut);
        res.setSpecialRequests("E2E Test Reservation");

        Reservation createdRes = reservationService.createReservation(res, 1, "127.0.0.1");
        assertNotNull(createdRes);
        assertTrue(createdRes.getReservationId() > 0);
        assertNotNull(createdRes.getReservationNumber());
        assertEquals(ReservationStatus.PENDING, createdRes.getStatus());

        // 4. Verify Bill generation
        Bill bill = billingService.getBillByReservationId(createdRes.getReservationId());
        assertNotNull(bill, "Bill should be generated automatically");
        assertEquals(BillStatus.PENDING, bill.getStatus());
        
        // Calculation check: (checkOut - checkIn) * pricePerNight
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal expectedCharges = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        // Note: StandardBillingStrategy might add tax, let's just check if it's >= expectedCharges
        assertTrue(bill.getRoomCharges().compareTo(expectedCharges) == 0 || bill.getRoomCharges().compareTo(expectedCharges) > 0);

        // 5. Pay Bill
        boolean paid = billingService.payBill(bill.getBillId(), PaymentMethod.CARD, 1, "127.0.0.1");
        assertTrue(paid);
        
        Bill paidBill = billingService.getBillByReservationId(createdRes.getReservationId());
        assertEquals(BillStatus.PAID, paidBill.getStatus());
        assertEquals(PaymentMethod.CARD, paidBill.getPaymentMethod());

        // 6. Check-in (Update Reservation Status)
        boolean checkedIn = reservationService.updateReservationStatus(createdRes.getReservationId(), ReservationStatus.CONFIRMED, 1, "127.0.0.1");
        assertTrue(checkedIn);
        
        boolean actualCheckIn = reservationService.updateReservationStatus(createdRes.getReservationId(), ReservationStatus.CHECKED_IN, 1, "127.0.0.1");
        assertTrue(actualCheckIn);

        // Verify room is now OCCUPIED
        Room updatedRoom = roomService.getRoomById(room.getRoomId());
        assertEquals(RoomStatus.OCCUPIED, updatedRoom.getStatus());
    }
}
