package com.oceanview.reservation.service;

import com.oceanview.reservation.dao.DAOFactory;
import com.oceanview.reservation.dao.UserDAO;
import com.oceanview.reservation.model.User;
import com.oceanview.reservation.util.JwtUtil;
import com.oceanview.reservation.util.PasswordUtil;

import java.util.Optional;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = DAOFactory.getUserDAO();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<String> login(String username, String password) {
        return userDAO.findByUsername(username)
            .filter(User::isActive)
            .filter(user -> PasswordUtil.checkPassword(password, user.getPasswordHash()))
            .map(JwtUtil::generateToken);
    }
}
