package com.example.gateway.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * JWT Authentication filter for the API Gateway.
 * Validates JWT tokens and passes them to downstream services.
 * Auth-service handles its own security - gateway just validates and passes tokens.
 */
@Component
public class JwtAuthFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();
        System.out.println("GATEWAY FILTER HIT: " + exchange.getRequest().getMethod() + " " + path);

        // PUBLIC auth endpoints - no token required
        if (isPublicAuthEndpoint(path)) {
            return chain.filter(exchange);
        }

        // Extract token from Authorization header or cookie
        String token = extractToken(exchange);

        // If no token found, allow gateway to pass through - let downstream service decide
        if (token == null) {
            return chain.filter(exchange);
        }

        // Validate token if present
        if (!jwtUtil.validate(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Build authentication context with valid token
        try {
            String username = jwtUtil.extractUsername(token);
            List<String> roles = jwtUtil.extractRoles(token);

            var authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        } catch (Exception ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicAuthEndpoint(String path) {
        return path.equals("/auth-service/api/auth/register") ||
                path.equals("/auth-service/api/auth/login") ||
                path.equals("/auth-service/api/auth/forgot-password") ||
                path.equals("/auth-service/api/auth/reset-password") ||
                path.equals("/auth-service/api/auth/verify-email");
    }

    private String extractToken(ServerWebExchange exchange) {
        // Try cookie first
        if (exchange.getRequest().getCookies().getFirst("JoJosCookie") != null) {
            return exchange.getRequest().getCookies().getFirst("JoJosCookie").getValue();
        }

        // Fallback to Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}
