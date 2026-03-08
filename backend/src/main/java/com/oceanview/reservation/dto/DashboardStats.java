package com.oceanview.reservation.dto;

import java.math.BigDecimal;

public class DashboardStats {
    private int todayCheckIns;
    private int todayCheckOuts;
    private int currentOccupancy;
    private int totalRooms;
    private BigDecimal monthlyRevenue;
    private int totalGuestsThisMonth;

    public int getTodayCheckIns() { return todayCheckIns; }
    public void setTodayCheckIns(int todayCheckIns) { this.todayCheckIns = todayCheckIns; }

    public int getTodayCheckOuts() { return todayCheckOuts; }
    public void setTodayCheckOuts(int todayCheckOuts) { this.todayCheckOuts = todayCheckOuts; }

    public int getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(int currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }

    public BigDecimal getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }

    public int getTotalGuestsThisMonth() { return totalGuestsThisMonth; }
    public void setTotalGuestsThisMonth(int totalGuestsThisMonth) { this.totalGuestsThisMonth = totalGuestsThisMonth; }
}
