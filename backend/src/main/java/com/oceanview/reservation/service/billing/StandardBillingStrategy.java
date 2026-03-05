package com.oceanview.reservation.service.billing;

import com.oceanview.reservation.model.Reservation;
import com.oceanview.reservation.model.Room;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

public class StandardBillingStrategy implements BillingStrategy {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% tax

    @Override
    public BigDecimal calculateRoomCharges(Reservation reservation, Room room) {
        long nights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (nights <= 0) nights = 1; // Minimum charge is 1 night
        return room.getPricePerNight().multiply(new BigDecimal(nights));
    }

    @Override
    public BigDecimal calculateTotalAmount(BigDecimal roomCharges) {
        BigDecimal taxAmount = roomCharges.multiply(TAX_RATE);
        return roomCharges.add(taxAmount);
    }
}
