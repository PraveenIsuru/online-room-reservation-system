package com.oceanview.reservation.dto;

import com.oceanview.reservation.model.enums.BillStatus;
import com.oceanview.reservation.model.enums.PaymentMethod;
import com.oceanview.reservation.model.enums.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BillDTO {
    private int billId;
    private int reservationId;
    private String reservationNumber;
    private String guestName;
    private String contactNumber;
    private String roomNumber;
    private RoomType roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long nights;
    private BigDecimal pricePerNight;
    private BigDecimal roomCharges;
    private BigDecimal totalAmount;
    private BillStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime issuedDate;
    private LocalDateTime paidDate;

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public long getNights() { return nights; }
    public void setNights(long nights) { this.nights = nights; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public BigDecimal getRoomCharges() { return roomCharges; }
    public void setRoomCharges(BigDecimal roomCharges) { this.roomCharges = roomCharges; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BillStatus getStatus() { return status; }
    public void setStatus(BillStatus status) { this.status = status; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDateTime issuedDate) { this.issuedDate = issuedDate; }

    public LocalDateTime getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDateTime paidDate) { this.paidDate = paidDate; }
}
