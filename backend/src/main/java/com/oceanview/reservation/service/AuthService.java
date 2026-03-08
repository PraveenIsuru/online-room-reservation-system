package com.oceanview.reservation.service;

import com.oceanview.reservation.dao.DAOFactory;
import com.oceanview.reservation.dao.UserDAO;
import com.oceanview.reservation.dto.LoginResponse;
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

    public Optional<LoginResponse> login(String username, String password) {
        return userDAO.findByUsername(username)
            .filter(User::isActive)
            .filter(user -> PasswordUtil.checkPassword(password, user.getPasswordHash()))
            .map(user -> {
                String token = JwtUtil.generateToken(user);
                return new LoginResponse(user, token);
            });
    }
}
