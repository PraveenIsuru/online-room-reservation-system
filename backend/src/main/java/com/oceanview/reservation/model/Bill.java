package com.oceanview.reservation.model;

import com.oceanview.reservation.model.enums.BillStatus;
import com.oceanview.reservation.model.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {
    private int billId;
    private int reservationId;
    private BigDecimal roomCharges;
    private BigDecimal totalAmount;
    private BillStatus status;
    private PaymentMethod paymentMethod; // nullable until paid
    private LocalDateTime issuedDate;
    private LocalDateTime paidDate; // nullable
    private LocalDateTime updatedAt;

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

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

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
