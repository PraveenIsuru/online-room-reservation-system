package com.oceanview.reservation.dao;

import com.oceanview.reservation.dao.impl.*;

public class DAOFactory {

    private static ReservationDAO reservationDAO;
    private static UserDAO userDAO;
    private static GuestDAO guestDAO;
    private static RoomDAO roomDAO;
    private static BillDAO billDAO;
    private static AuditDAO auditDAO;

    public static synchronized ReservationDAO getReservationDAO() {
        if (reservationDAO == null) {
            reservationDAO = new ReservationDAOImpl();
        }
        return reservationDAO;
    }

    public static synchronized UserDAO getUserDAO() {
        if (userDAO == null) {
            userDAO = new UserDAOImpl();
        }
        return userDAO;
    }

    public static synchronized GuestDAO getGuestDAO() {
        if (guestDAO == null) {
            guestDAO = new GuestDAOImpl();
        }
        return guestDAO;
    }

    public static synchronized RoomDAO getRoomDAO() {
        if (roomDAO == null) {
            roomDAO = new RoomDAOImpl();
        }
        return roomDAO;
    }

    public static synchronized BillDAO getBillDAO() {
        if (billDAO == null) {
            billDAO = new BillDAOImpl();
        }
        return billDAO;
    }

    public static synchronized AuditDAO getAuditDAO() {
        if (auditDAO == null) {
            auditDAO = new AuditDAOImpl();
        }
        return auditDAO;
    }
}
