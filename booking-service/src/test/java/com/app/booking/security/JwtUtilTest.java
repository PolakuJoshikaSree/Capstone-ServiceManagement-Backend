package com.app.booking.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtUtilTest {

    @Test
    void validateToken_success() {

        String secret = "thisIsASecretKeyThatIsAtLeast32CharsLong";
        Key key = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                SignatureAlgorithm.HS256.getJcaName()
        );

        String token = Jwts.builder()
                .setSubject("user1")
                .claim("role", "CUSTOMER")
                .setIssuedAt(new Date())
                .signWith(key)
                .compact();

        JwtUtil jwtUtil = new JwtUtil(secret);

        assertNotNull(jwtUtil.validateToken(token));
    }
}
