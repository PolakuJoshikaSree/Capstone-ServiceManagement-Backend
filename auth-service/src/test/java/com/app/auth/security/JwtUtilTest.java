package com.app.auth.security;

import com.app.auth.entity.UserEntity;
import com.app.auth.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        // secret, access expiry (seconds), refresh expiry (seconds)
        jwtUtil = new JwtUtil(
                "test-secret-key-test-secret-key-test-secret-key",
                3600,
                7200
        );
    }

    private UserEntity mockUser() {
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setEmail("a@b.com");
        user.setRole(Role.CUSTOMER);
        return user;
    }
    @Test
    void generateAccessToken_success() {
        String token = jwtUtil.generateAccessToken(mockUser());
        assertNotNull(token);
    }
    @Test
    void generateAccessToken_withRefreshFlag() {
        String token = jwtUtil.generateAccessToken(mockUser(), true);
        assertNotNull(token);
    }
    @Test
    void generateRefreshToken_success() {
        String token = jwtUtil.generateRefreshToken(mockUser());
        assertNotNull(token);
    }
    @Test
    void getIssuedAt_success() {
        String token = jwtUtil.generateAccessToken(mockUser());
        LocalDateTime issuedAt = jwtUtil.getIssuedAt(token);
        assertNotNull(issuedAt);
    }
    @Test
    void getExpiresAt_success() {
        String token = jwtUtil.generateAccessToken(mockUser());

        LocalDateTime issuedAt = jwtUtil.getIssuedAt(token);
        LocalDateTime expiresAt = jwtUtil.getExpiresAt(token);

        assertNotNull(expiresAt);
        assertNotNull(issuedAt);
        assertTrue(expiresAt.isAfter(issuedAt));
    }

    @Test
    void validateToken_success() {
        String token = jwtUtil.generateAccessToken(mockUser());
        assertDoesNotThrow(() -> jwtUtil.validateToken(token));
    }
}
