package com.app.auth.service;

import com.app.auth.dto.JwtTokenDTO;
import com.app.auth.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class JwtTokenServiceTest {

    RedisTemplate<String, JwtTokenDTO> redis = mock(RedisTemplate.class);
    StringRedisTemplate stringRedis = mock(StringRedisTemplate.class);
    JwtUtil jwtUtil = mock(JwtUtil.class);

    JwtTokenService service = new JwtTokenService(redis, stringRedis, jwtUtil);

    @Test
    void storeTokenInRedis_accessToken() {
        JwtTokenDTO dto = new JwtTokenDTO();
        dto.setIssuedAt(LocalDateTime.now());
        dto.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        ValueOperations<String, JwtTokenDTO> ops = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(ops);

        service.storeTokenInRedis("token", dto);

        verify(ops).set(startsWith("token:access:"), eq(dto), any());
    }

    @Test
    void getTokenFromRedis_fallbackToRefresh() {
        ValueOperations<String, JwtTokenDTO> ops = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(ops);
        when(ops.get(any())).thenReturn(null, new JwtTokenDTO());

        assertNotNull(service.getTokenFromRedis("token"));
    }

    @Test
    void removeTokenFromRedis_success() {
        service.removeTokenFromRedis("token");

        verify(redis).delete("token:access:token");
        verify(redis).delete("token:refresh:token");
    }


    @Test
    void removeAllTokensForUser_safe() {
        when(redis.keys("token:access:*"))
                .thenReturn(Set.of("k1"));
        when(redis.keys("token:refresh:*"))
                .thenReturn(Set.of("k2"));

        JwtTokenDTO dto = new JwtTokenDTO();
        dto.setUserId("1");

        ValueOperations<String, JwtTokenDTO> ops = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(ops);
        when(ops.get(anyString())).thenReturn(dto);

        service.removeAllTokensForUser("1");

        verify(redis, atLeastOnce()).delete(anyString());
    }

}
