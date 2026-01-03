package com.app.booking.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthFilter authFilter;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // -------- NO AUTH HEADER --------
    @Test
    void noAuthorizationHeader_allowsRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        authFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    // -------- NOT BEARER TOKEN --------
    @Test
    void invalidAuthorizationHeader_allowsRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");

        MockHttpServletResponse response = new MockHttpServletResponse();

        authFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    // -------- VALID TOKEN --------
    @Test
    void validToken_setsAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validtoken");

        MockHttpServletResponse response = new MockHttpServletResponse();

        Claims claims = mock(Claims.class);
        when(jwtUtil.validateToken("validtoken")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("user1");
        when(claims.get("role", String.class)).thenReturn("CUSTOMER");

        authFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals("user1",
                SecurityContextHolder.getContext()
                        .getAuthentication().getName());
    }

    // -------- INVALID TOKEN --------
    @Test
    void invalidToken_returns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer badtoken");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.validateToken("badtoken"))
                .thenThrow(new RuntimeException("Invalid"));

        authFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        verify(filterChain, never()).doFilter(any(), any());
    }
}
