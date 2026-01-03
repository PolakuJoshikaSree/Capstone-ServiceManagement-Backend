package com.app.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    @Test
    void optionsRequest_shouldPassThrough() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil);

        MockServerWebExchange exchange =
                MockServerWebExchange.from(
                        MockServerHttpRequest
                                .method(HttpMethod.OPTIONS, "/any")
                                .build()
                );

        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        verify(chain).filter(any());
    }

    @Test
    void noAuthorizationHeader_shouldPassThrough() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil);

        MockServerWebExchange exchange =
                MockServerWebExchange.from(
                        MockServerHttpRequest.get("/service").build()
                );

        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        verify(chain).filter(any());
    }

    @Test
    void invalidToken_setsUnauthorized() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        when(jwtUtil.validate("bad")).thenReturn(false);

        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil);

        MockServerWebExchange exchange =
                MockServerWebExchange.from(
                        MockServerHttpRequest.get("/service")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer bad")
                                .build()
                );

        WebFilterChain chain = mock(WebFilterChain.class);

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any());
    }

    @Test
    void validToken_setsHeadersAndPasses() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        when(jwtUtil.validate("good")).thenReturn(true);
        when(jwtUtil.extractUsername("good")).thenReturn("user1");
        when(jwtUtil.extractRoles("good")).thenReturn(
                java.util.List.of("ROLE_USER")
        );

        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil);

        MockServerWebExchange exchange =
                MockServerWebExchange.from(
                        MockServerHttpRequest.get("/service")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer good")
                                .build()
                );

        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        verify(chain).filter(any());
        assertEquals(
                "user1",
                exchange.getRequest().getHeaders().getFirst("X-USER-ID")
        );
    }
}
