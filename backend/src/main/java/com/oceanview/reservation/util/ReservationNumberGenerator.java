package com.oceanview.reservation.util;

import com.oceanview.reservation.dao.DAOFactory;
import java.time.Year;

public class ReservationNumberGenerator {

    private static volatile ReservationNumberGenerator instance;

    private ReservationNumberGenerator() {}

    public static ReservationNumberGenerator getInstance() {
        if (instance == null) {
            synchronized (ReservationNumberGenerator.class) {
                if (instance == null) {
                    instance = new ReservationNumberGenerator();
                }
            }
        }
        return instance;
    }

    public synchronized String generate() {
        int year = Year.now().getValue();
        int seq  = DAOFactory.getReservationDAO().getNextSequenceForYear(year);
        return String.format("RES-%d-%05d", year, seq);
    }
}
