package com.oceanview.reservation.dao;

import com.oceanview.reservation.model.Guest;
import java.util.List;

public interface GuestDAO {
    List<Guest> findAll();
    Guest findById(int guestId);
    Guest findByContact(String contactOrEmail);
    int create(Guest guest);
    boolean update(Guest guest);
    List<Guest> findWithPagination(int offset, int limit, String search);
    int countGuests(String search);
}
