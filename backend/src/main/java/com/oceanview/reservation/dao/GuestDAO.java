package com.oceanview.reservation.dao;

import com.oceanview.reservation.model.Guest;
import java.util.List;

public interface GuestDAO {
    List<Guest> findAll();
    Guest findById(int guestId);
    Guest findByContact(String contactOrEmail);
    int create(Guest guest);
    boolean update(Guest guest);
}
