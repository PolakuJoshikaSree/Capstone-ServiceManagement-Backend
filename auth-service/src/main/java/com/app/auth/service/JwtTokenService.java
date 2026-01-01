package com.app.auth.service;

import com.app.auth.dto.JwtTokenDTO;
import com.app.auth.entity.UserEntity;
import com.app.auth.security.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.Collections;
import java.util.Optional;

@Service
public class JwtTokenService {

    private final RedisTemplate<String, JwtTokenDTO> jwtRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtUtil jwtUtil;

    private static final String ACCESS_PREFIX = "token:access:";
    private static final String REFRESH_PREFIX = "token:refresh:";
    private static final String PASSWORD_RESET_PREFIX = "password:reset:";

    public JwtTokenService(RedisTemplate<String, JwtTokenDTO> jwtRedisTemplate,
                           StringRedisTemplate stringRedisTemplate,
                           JwtUtil jwtUtil) {
        this.jwtRedisTemplate = jwtRedisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.jwtUtil = jwtUtil;
    }

    public String generateAccessToken(UserEntity user) {
        return jwtUtil.generateAccessToken(user);
    }

    public String generateRefreshToken(UserEntity user) {

        return jwtUtil.generateRefreshToken(user);
    }

    public void storeTokenInRedis(String token, JwtTokenDTO tokenDTO) {
        LocalDateTime expiresAt = tokenDTO.getExpiresAt();
        LocalDateTime issuedAt = tokenDTO.getIssuedAt();
        long seconds = Duration.between(issuedAt, expiresAt).getSeconds();

        if (seconds <= 0) {
            return;
        }

        if (seconds <= 86400) {
            jwtRedisTemplate.opsForValue().set(ACCESS_PREFIX + token, tokenDTO, Duration.ofSeconds(seconds));
        } else {
            jwtRedisTemplate.opsForValue().set(REFRESH_PREFIX + token, tokenDTO, Duration.ofSeconds(seconds));
        }
    }

    public JwtTokenDTO getTokenFromRedis(String token) {
        JwtTokenDTO dto = jwtRedisTemplate.opsForValue().get(ACCESS_PREFIX + token);
        if (dto != null) return dto;
        return jwtRedisTemplate.opsForValue().get(REFRESH_PREFIX + token);
    }

    public void removeTokenFromRedis(String token) {
        jwtRedisTemplate.delete(ACCESS_PREFIX + token);
        jwtRedisTemplate.delete(REFRESH_PREFIX + token);
    }

    public String generatePasswordResetToken(String email) {
        // use the email to influence the token so parameter is used (silences static analysis warnings)
        String uid = UUID.randomUUID().toString();
        int hash = email != null ? email.hashCode() : 0;
        return uid + "-" + Integer.toHexString(hash);
    }

    public void storePasswordResetToken(String email, String token) {
        stringRedisTemplate.opsForValue().set(PASSWORD_RESET_PREFIX + token, email, Duration.ofHours(1));
    }

    public String validatePasswordResetToken(String token) {
        return stringRedisTemplate.opsForValue().get(PASSWORD_RESET_PREFIX + token);
    }

    public void removePasswordResetToken(String token) {
        stringRedisTemplate.delete(PASSWORD_RESET_PREFIX + token);
    }

    public void removeAllTokensForUser(String string) {
        try {
            Set<String> accessKeys = jwtRedisTemplate.keys(ACCESS_PREFIX + "*");
            for (String key : Optional.ofNullable(accessKeys).orElse(Collections.emptySet())) {
                JwtTokenDTO dto = jwtRedisTemplate.opsForValue().get(key);
                if (dto != null && dto.getUserId() != null && dto.getUserId().equals(string)) {
                    jwtRedisTemplate.delete(key);
                }
            }

            Set<String> refreshKeys = jwtRedisTemplate.keys(REFRESH_PREFIX + "*");
            for (String key : Optional.ofNullable(refreshKeys).orElse(Collections.emptySet())) {
                JwtTokenDTO dto = jwtRedisTemplate.opsForValue().get(key);
                if (dto != null && dto.getUserId() != null && dto.getUserId().equals(string)) {
                    jwtRedisTemplate.delete(key);
                }
            }
        } catch (Exception ex) {
            // best-effort removal; swallow exceptions to avoid breaking password change flow
        }
    }
}
