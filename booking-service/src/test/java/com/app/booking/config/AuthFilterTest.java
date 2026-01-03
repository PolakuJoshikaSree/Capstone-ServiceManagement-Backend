package com.app.booking.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.app.booking.security.AuthFilter;
import com.app.booking.security.JwtUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthFilter authFilter;

    @Test
    void invalidToken_returns401WithJson() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer badtoken");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        doThrow(new RuntimeException())
                .when(jwtUtil).validateToken("badtoken");

        authFilter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
    }
}
