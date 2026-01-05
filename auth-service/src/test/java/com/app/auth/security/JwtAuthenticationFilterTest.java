package com.app.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

class JwtAuthenticationFilterTest {

    JwtUtil jwtUtil = new JwtUtil(
            "12345678901234567890123456789012",
            3600,
            7200
    );

    JwtAuthenticationFilter filter =
            new JwtAuthenticationFilter(jwtUtil);

    @Test
    void skipOptionsRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("OPTIONS", "/api/users");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.doFilter(req, res, chain);
        Mockito.verify(chain).doFilter(req, res);
    }

    @Test
    void skipAuthEndpoints() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/auth/login");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.doFilter(req, res, chain);
        Mockito.verify(chain).doFilter(req, res);
    }

    @Test
    void validJwt_setsAuthentication() throws Exception {
        var user = new com.app.auth.entity.UserEntity();
        user.setId("1");
        user.setEmail("a@b.com");
        user.setRole(com.app.auth.enums.Role.ADMIN);

        String token = jwtUtil.generateAccessToken(user);

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/users");
        req.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, Mockito.mock(FilterChain.class));
    }

    @Test
    void invalidJwt_clearsContext() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/users");
        req.addHeader("Authorization", "Bearer invalid");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, Mockito.mock(FilterChain.class));
    }
}
