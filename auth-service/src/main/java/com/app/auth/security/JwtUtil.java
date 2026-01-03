package com.app.auth.security;

import com.app.auth.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long accessTokenExpirationSeconds;
    private final long refreshTokenExpirationSeconds;

    private static final long RESTRICTED_TOKEN_EXPIRATION_SECONDS = 900;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.accessTokenExpirationSeconds:86400}") long accessTokenExpirationSeconds,
            @Value("${jwt.refreshTokenExpirationSeconds:604800}") long refreshTokenExpirationSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
    }

    public String generateAccessToken(UserEntity user) {
        return generateAccessToken(user, false);
    }

    public String generateAccessToken(UserEntity user, boolean pwdExpired) {

        Instant now = Instant.now();

        JwtBuilder builder = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))   // ✅ USER ID
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTokenExpirationSeconds)));

        if (pwdExpired) {
            builder.claim("pwd_expired", true);
        }

        return builder
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserEntity user) {

        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))   // ✅ USER ID
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshTokenExpirationSeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public LocalDateTime getIssuedAt(String token) {
        Date d = validateToken(token).getBody().getIssuedAt();
        return LocalDateTime.ofInstant(d.toInstant(), ZoneOffset.UTC);
    }

    public LocalDateTime getExpiresAt(String token) {
        Date d = validateToken(token).getBody().getExpiration();
        return LocalDateTime.ofInstant(d.toInstant(), ZoneOffset.UTC);
    }
}
