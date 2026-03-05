package com.oceanview.reservation.dto;

import java.math.BigDecimal;

public class DashboardStats {
    private int totalGuests;
    private int activeReservations;
    private long availableRooms;
    private BigDecimal totalRevenue;

    public int getTotalGuests() { return totalGuests; }
    public void setTotalGuests(int totalGuests) { this.totalGuests = totalGuests; }

    public int getActiveReservations() { return activeReservations; }
    public void setActiveReservations(int activeReservations) { this.activeReservations = activeReservations; }

    public long getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(long availableRooms) { this.availableRooms = availableRooms; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}
