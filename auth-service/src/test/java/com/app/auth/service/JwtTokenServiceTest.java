package com.app.auth.service;

import com.app.auth.dto.JwtTokenDTO;
import com.app.auth.entity.UserEntity;
import com.app.auth.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenServiceTest {

    RedisTemplate<String, JwtTokenDTO> redisTemplate = mock(RedisTemplate.class);
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    JwtUtil jwtUtil = mock(JwtUtil.class);

    JwtTokenService service =
            new JwtTokenService(redisTemplate, stringRedisTemplate, jwtUtil);

    @Test
    void generateTokens_delegatesToJwtUtil() {
        UserEntity user = new UserEntity();

        when(jwtUtil.generateAccessToken(user)).thenReturn("access");
        when(jwtUtil.generateRefreshToken(user)).thenReturn("refresh");

        assertEquals("access", service.generateAccessToken(user));
        assertEquals("refresh", service.generateRefreshToken(user));
    }

    @Test
    void storeTokenInRedis_accessToken() {
        JwtTokenDTO dto = new JwtTokenDTO();
        dto.setIssuedAt(LocalDateTime.now());
        dto.setExpiresAt(LocalDateTime.now().plusSeconds(100));

        ValueOperations<String, JwtTokenDTO> ops = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(ops);

        service.storeTokenInRedis("token", dto);

        verify(ops).set(startsWith("token:access:"), eq(dto), any());
    }

    @Test
    void storeTokenInRedis_zeroSeconds_doesNothing() {
        JwtTokenDTO dto = new JwtTokenDTO();
        dto.setIssuedAt(LocalDateTime.now());
        dto.setExpiresAt(LocalDateTime.now());

        service.storeTokenInRedis("token", dto);

        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    void getTokenFromRedis_accessFound() {
        ValueOperations<String, JwtTokenDTO> ops = mock(ValueOperations.class);
        JwtTokenDTO dto = new JwtTokenDTO();

        when(redisTemplate.opsForValue()).thenReturn(ops);
        when(ops.get(anyString())).thenReturn(dto);

        assertEquals(dto, service.getTokenFromRedis("token"));
    }

    @Test
    void removeTokenFromRedis_deletesBoth() {
        service.removeTokenFromRedis("token");

        verify(redisTemplate).delete("token:access:token");
        verify(redisTemplate).delete("token:refresh:token");
    }

    @Test
    void passwordResetToken_flow() {
        ValueOperations<String, String> ops = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(ops);

        String token = service.generatePasswordResetToken("a@b.com");
        assertNotNull(token);

        service.storePasswordResetToken("a@b.com", token);
        service.validatePasswordResetToken(token);
        service.removePasswordResetToken(token);

        verify(stringRedisTemplate).delete("password:reset:" + token);
    }

    @Test
    void removeAllTokensForUser_safeExecution() {
        when(redisTemplate.keys(anyString())).thenReturn(Set.of());

        service.removeAllTokensForUser("user");
    }
}
