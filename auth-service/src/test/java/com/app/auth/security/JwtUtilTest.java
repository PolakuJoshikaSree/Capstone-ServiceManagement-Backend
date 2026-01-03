package com.app.auth.security;

import com.app.auth.entity.UserEntity;
import com.app.auth.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    // 256-bit key (minimum required for HS256)
    private static final String SECRET =
            "01234567890123456789012345678901";

    private final JwtUtil jwtUtil =
            new JwtUtil(SECRET, 60, 120);

    private UserEntity createUser() {
        UserEntity user = new UserEntity();
        user.setId("user-123");
        user.setEmail("user@test.com");
        user.setRole(Role.CUSTOMER);
        return user;
    }

    @Test
    void generateAccessToken_withoutPasswordExpired() {
        UserEntity user = createUser();

        String token = jwtUtil.generateAccessToken(user);

        assertNotNull(token);

        Jws<Claims> claims = jwtUtil.validateToken(token);
        assertEquals("user-123", claims.getBody().getSubject());
        assertEquals("user@test.com", claims.getBody().get("email"));
        assertEquals("CUSTOMER", claims.getBody().get("role"));
        assertNull(claims.getBody().get("pwd_expired"));
    }

    @Test
    void generateAccessToken_withPasswordExpired() {
        UserEntity user = createUser();

        String token = jwtUtil.generateAccessToken(user, true);

        Claims claims = jwtUtil.validateToken(token).getBody();
        assertEquals(true, claims.get("pwd_expired"));
    }

    @Test
    void generateRefreshToken_success() {
        UserEntity user = createUser();

        String token = jwtUtil.generateRefreshToken(user);

        assertNotNull(token);

        Claims claims = jwtUtil.validateToken(token).getBody();
        assertEquals("user-123", claims.getSubject());
        assertEquals("CUSTOMER", claims.get("role"));
    }

    @Test
    void getIssuedAt_and_getExpiresAt() {
        UserEntity user = createUser();
        String token = jwtUtil.generateAccessToken(user);

        LocalDateTime issuedAt = jwtUtil.getIssuedAt(token);
        LocalDateTime expiresAt = jwtUtil.getExpiresAt(token);

        assertNotNull(issuedAt);
        assertNotNull(expiresAt);
        assertTrue(expiresAt.isAfter(issuedAt));
    }
}
