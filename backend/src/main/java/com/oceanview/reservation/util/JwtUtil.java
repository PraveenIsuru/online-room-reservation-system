package com.oceanview.reservation.util;

import com.oceanview.reservation.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

public class JwtUtil {

    private static final String SECRET_KEY;
    private static final long EXPIRY_MS;

    static {
        try {
            Properties props = new Properties();
            props.load(JwtUtil.class.getResourceAsStream("/app.properties"));
            SECRET_KEY = props.getProperty("jwt.secret");
            EXPIRY_MS  = Long.parseLong(props.getProperty("jwt.expiry.hours")) * 3600 * 1000L;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JWT config", e);
        }
    }

    public static String generateToken(User user) {
        return Jwts.builder()
            .subject(String.valueOf(user.getUserId()))
            .claim("username", user.getUsername())
            .claim("role",     user.getRole().name())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
            .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
            .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
