package com.oceanview.reservation.dao;

import com.oceanview.reservation.dao.impl.ReservationDAOImpl;
import com.oceanview.reservation.dao.impl.UserDAOImpl;

public class DAOFactory {

    private static ReservationDAO reservationDAO;
    private static UserDAO userDAO;

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
}
