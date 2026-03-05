package com.oceanview.reservation.dao;

import com.oceanview.reservation.model.User;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findByUsername(String username);
}
