package com.oceanview.reservation.service;

import com.oceanview.reservation.dao.UserDAO;
import com.oceanview.reservation.dto.LoginResponse;
import com.oceanview.reservation.model.User;
import com.oceanview.reservation.model.enums.UserRole;
import com.oceanview.reservation.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userDAO);
    }

    @Test
    void login_Success() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hashedPassword);
        user.setActive(true);
        user.setRole(UserRole.STAFF);

        when(userDAO.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        Optional<LoginResponse> response = authService.login(username, password);

        // Assert
        assertTrue(response.isPresent());
        assertNotNull(response.get().getToken());
        assertEquals(username, response.get().getUsername());
        verify(userDAO).findByUsername(username);
    }

    @Test
    void login_InvalidPassword() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hashedPassword);
        user.setActive(true);

        when(userDAO.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        Optional<LoginResponse> response = authService.login(username, wrongPassword);

        // Assert
        assertFalse(response.isPresent());
    }

    @Test
    void login_InactiveUser() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hashedPassword);
        user.setActive(false);

        when(userDAO.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        Optional<LoginResponse> response = authService.login(username, password);

        // Assert
        assertFalse(response.isPresent());
    }

    @Test
    void login_UserNotFound() {
        // Arrange
        String username = "nonexistent";
        String password = "password123";

        when(userDAO.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Optional<LoginResponse> response = authService.login(username, password);

        // Assert
        assertFalse(response.isPresent());
    }
}
