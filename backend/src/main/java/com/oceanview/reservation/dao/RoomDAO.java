package com.oceanview.reservation.dao;

import com.oceanview.reservation.model.Room;
import com.oceanview.reservation.model.enums.RoomStatus;
import com.oceanview.reservation.model.enums.RoomType;
import java.time.LocalDate;
import java.util.List;

public interface RoomDAO {
    List<Room> findAll();
    List<Room> findAvailable(LocalDate checkIn, LocalDate checkOut, RoomType type);
    boolean updateStatus(int roomId, RoomStatus status);
    Room findById(int roomId);
}