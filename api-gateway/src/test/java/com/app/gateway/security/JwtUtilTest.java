package com.app.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET =
            "mysecretkeymysecretkeymysecretkeymysecretkey";

    @Test
    void validate_and_extractClaims_success() {

        JwtUtil util = new JwtUtil();
        ReflectionTestUtils.setField(util, "jwtSecret", SECRET);

        Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .setSubject("user1")
                .claim("role", "ADMIN")
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertTrue(util.validate(token));
        assertEquals("user1", util.extractUsername(token));

        List<String> roles = util.extractRoles(token);
        assertEquals(1, roles.size());
        assertEquals("ROLE_ADMIN", roles.get(0));
    }

    @Test
    void validate_invalidToken_returnsFalse() {
        JwtUtil util = new JwtUtil();
        ReflectionTestUtils.setField(util, "jwtSecret", SECRET);

        assertFalse(util.validate("invalid.token.value"));
    }
}
