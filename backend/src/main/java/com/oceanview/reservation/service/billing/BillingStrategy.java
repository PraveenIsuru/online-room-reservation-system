package com.oceanview.reservation.service.billing;

import com.oceanview.reservation.model.Reservation;
import com.oceanview.reservation.model.Room;
import java.math.BigDecimal;

public interface BillingStrategy {
    BigDecimal calculateRoomCharges(Reservation reservation, Room room);
    BigDecimal calculateTotalAmount(BigDecimal roomCharges);
}
