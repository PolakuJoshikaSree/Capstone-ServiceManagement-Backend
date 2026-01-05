package com.app.booking.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.*;

import static org.junit.jupiter.api.Assertions.*;

class AuthFilterTest {

    @Test
    void validToken_setsAuthentication() throws Exception {

        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        Claims claims = Mockito.mock(Claims.class);

        Mockito.when(claims.getSubject()).thenReturn("user1");
        Mockito.when(claims.get("role", String.class)).thenReturn("CUSTOMER");
        Mockito.when(jwtUtil.validateToken("token")).thenReturn(claims);

        AuthFilter filter = new AuthFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertNotNull(
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication()
        );
    }

    @Test
    void invalidToken_returns401() throws Exception {

        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        Mockito.when(jwtUtil.validateToken(Mockito.any()))
                .thenThrow(new RuntimeException());

        AuthFilter filter = new AuthFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer bad");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertEquals(401, response.getStatus());
    }
}
