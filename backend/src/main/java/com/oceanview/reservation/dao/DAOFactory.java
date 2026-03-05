package com.oceanview.reservation.dao;

import com.oceanview.reservation.dao.impl.ReservationDAOImpl;

public class DAOFactory {

    private static ReservationDAO reservationDAO;

    public static synchronized ReservationDAO getReservationDAO() {
        if (reservationDAO == null) {
            reservationDAO = new ReservationDAOImpl();
        }
        return reservationDAO;
    }
}
