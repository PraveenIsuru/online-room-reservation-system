package com.oceanview.reservation.dao;

import com.oceanview.reservation.model.Reservation;
import com.oceanview.reservation.model.enums.ReservationStatus;
import java.time.LocalDate;

public interface ReservationDAO {
    int getNextSequenceForYear(int year);
    int create(Reservation reservation);
    boolean updateStatus(int reservationId, ReservationStatus status);
    boolean existsRoomConflict(int roomId, LocalDate checkIn, LocalDate checkOut, Integer excludeReservationId);
    Reservation findById(int reservationId);
    Reservation findByNumber(String reservationNumber);
}
