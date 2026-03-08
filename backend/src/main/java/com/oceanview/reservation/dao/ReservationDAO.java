package com.oceanview.reservation.dao;

import com.oceanview.reservation.dto.ReservationListDTO;
import com.oceanview.reservation.model.Reservation;
import com.oceanview.reservation.model.enums.ReservationStatus;
import java.time.LocalDate;
import java.util.List;

public interface ReservationDAO {
    int getNextSequenceForYear(int year);
    int create(Reservation reservation);
    boolean updateStatus(int reservationId, ReservationStatus status);
    boolean existsRoomConflict(int roomId, LocalDate checkIn, LocalDate checkOut, Integer excludeReservationId);
    Reservation findById(int reservationId);
    Reservation findByNumber(String reservationNumber);
    int getActiveReservationCount();
    List<Reservation> findAll();
    List<Reservation> findByCheckInDate(LocalDate date);
    List<Reservation> findByCheckOutDate(LocalDate date);
    List<Reservation> findWithPagination(int offset, int limit, ReservationStatus status);
    int countReservations(ReservationStatus status);
    List<ReservationListDTO> findEnrichedWithPagination(int offset, int limit, ReservationStatus status, String search, Integer guestId);
    int countEnrichedReservations(ReservationStatus status, String search, Integer guestId);
    ReservationListDTO findEnrichedById(int reservationId);
    ReservationListDTO findEnrichedByNumber(String reservationNumber);
}
