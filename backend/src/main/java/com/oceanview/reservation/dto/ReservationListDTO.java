package com.oceanview.reservation.dto;

import com.oceanview.reservation.model.enums.BillStatus;
import com.oceanview.reservation.model.enums.ReservationStatus;
import com.oceanview.reservation.model.enums.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationListDTO {
    private int reservationId;
    private String reservationNumber;
    private int guestId;
    private String guestName;
    private String contactNumber;
    private int roomId;
    private String roomNumber;
    private RoomType roomType;
    private BigDecimal pricePerNight;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long nights;
    private ReservationStatus status;
    private String specialRequests;
    private Integer billId;
    private BigDecimal totalAmount;
    private BillStatus billStatus;
    private LocalDateTime createdAt;
    private String createdByUsername;

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public long getNights() { return nights; }
    public void setNights(long nights) { this.nights = nights; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public Integer getBillId() { return billId; }
    public void setBillId(Integer billId) { this.billId = billId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BillStatus getBillStatus() { return billStatus; }
    public void setBillStatus(BillStatus billStatus) { this.billStatus = billStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }
}
