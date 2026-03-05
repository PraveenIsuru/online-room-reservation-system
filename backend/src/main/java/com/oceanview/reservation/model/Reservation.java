package com.oceanview.reservation.model;

import com.oceanview.reservation.model.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {
    private int reservationId;
    private String reservationNumber;
    private int guestId;
    private int roomId;
    private int createdBy;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private ReservationStatus status;
    private String specialRequests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
