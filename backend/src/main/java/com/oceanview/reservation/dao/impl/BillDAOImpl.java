package com.oceanview.reservation.dao.impl;

import com.oceanview.reservation.dao.BillDAO;
import com.oceanview.reservation.model.Bill;
import com.oceanview.reservation.model.enums.BillStatus;
import com.oceanview.reservation.model.enums.PaymentMethod;
import com.oceanview.reservation.util.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.*;

public class BillDAOImpl implements BillDAO {

    @Override
    public int create(Bill b) {
        String sql = "INSERT INTO bills (reservation_id, room_charges, total_amount, status, payment_method, issued_date, paid_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getReservationId());
            ps.setBigDecimal(2, b.getRoomCharges());
            ps.setBigDecimal(3, b.getTotalAmount());
            ps.setString(4, b.getStatus().name());
            if (b.getPaymentMethod() != null) ps.setString(5, b.getPaymentMethod().name()); else ps.setNull(5, Types.VARCHAR);
            if (b.getIssuedDate() != null) ps.setTimestamp(6, Timestamp.valueOf(b.getIssuedDate())); else ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            if (b.getPaidDate() != null) ps.setTimestamp(7, Timestamp.valueOf(b.getPaidDate())); else ps.setNull(7, Types.TIMESTAMP);
            int affected = ps.executeUpdate();
            if (affected == 0) throw new RuntimeException("Creating bill failed, no rows affected.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new RuntimeException("Creating bill failed, no ID obtained.");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating bill", e);
        }
    }

    @Override
    public Bill findByReservationId(int reservationId) {
        String sql = "SELECT * FROM bills WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bill by reservation id", e);
        }
    }

    @Override
    public boolean updateStatus(int billId, BillStatus status, PaymentMethod method) {
        String sql = "UPDATE bills SET status = ?, payment_method = ?, paid_date = ?, updated_at = CURRENT_TIMESTAMP WHERE bill_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            if (method != null) ps.setString(2, method.name()); else ps.setNull(2, Types.VARCHAR);
            if (status == BillStatus.PAID) ps.setTimestamp(3, new Timestamp(System.currentTimeMillis())); else ps.setNull(3, Types.TIMESTAMP);
            ps.setInt(4, billId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating bill status", e);
        }
    }

    @Override
    public java.math.BigDecimal getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM bills WHERE status = 'PAID'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal revenue = rs.getBigDecimal(1);
                return revenue != null ? revenue : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching total revenue", e);
        }
    }

    private Bill map(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setReservationId(rs.getInt("reservation_id"));
        b.setRoomCharges(rs.getBigDecimal("room_charges"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        String status = rs.getString("status");
        if (status != null) b.setStatus(BillStatus.valueOf(status));
        String method = rs.getString("payment_method");
        if (method != null) b.setPaymentMethod(PaymentMethod.valueOf(method));
        Timestamp issued = rs.getTimestamp("issued_date");
        if (issued != null) b.setIssuedDate(issued.toLocalDateTime());
        Timestamp paid = rs.getTimestamp("paid_date");
        if (paid != null) b.setPaidDate(paid.toLocalDateTime());
        Timestamp upd = rs.getTimestamp("updated_at");
        if (upd != null) b.setUpdatedAt(upd.toLocalDateTime());
        return b;
    }
}
