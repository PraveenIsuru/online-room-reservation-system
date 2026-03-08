package com.oceanview.reservation.dto;

import com.oceanview.reservation.model.User;
import com.oceanview.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;

public class LoginResponse {
    private int userId;
    private String username;
    private String role;
    private String token;
    private String expiresAt;

    public LoginResponse(User user, String token) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.role = user.getRole().name();
        this.token = token;

        // Extract expiration from token
        Claims claims = JwtUtil.validateToken(token);
        this.expiresAt = claims.getExpiration().toInstant().toString();
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
}
