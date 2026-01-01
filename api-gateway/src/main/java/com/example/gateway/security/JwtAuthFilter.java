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
 *
 * Responsibilities:
 * - Validate JWT token
 * - Extract user identity & roles
 * - Forward user context to downstream services via headers
 *
 * NOTE:
 * - Gateway does NOT enforce business authorization
 * - Downstream services decide access using headers
 */
@Component
public class JwtAuthFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // Allow preflight requests
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequest().getMethod().name())) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();
        System.out.println("GATEWAY FILTER HIT → " + exchange.getRequest().getMethod() + " " + path);

        // Public authentication endpoints
        if (isPublicAuthEndpoint(path)) {
            return chain.filter(exchange);
        }

        // Extract JWT token
        String token = extractToken(exchange);

        // If token is missing, let downstream service decide
        if (token == null) {
            return chain.filter(exchange);
        }

        // Validate token
        if (!jwtUtil.validate(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            // Extract details from JWT
            String username = jwtUtil.extractUsername(token);
            List<String> roles = jwtUtil.extractRoles(token);

            // Convert roles to Spring authorities
            var authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            // Mutate request to forward user context to downstream services
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(request -> request
                            .header("X-USER-ID", username)
                            .header("X-USER-ROLES", String.join(",", roles))
                    )
                    .build();

            return chain.filter(mutatedExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (Exception ex) {
            ex.printStackTrace();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * Public auth endpoints that do not require JWT
     */
    private boolean isPublicAuthEndpoint(String path) {
        return path.equals("/auth-service/api/auth/register")
                || path.equals("/auth-service/api/auth/login")
                || path.equals("/auth-service/api/auth/forgot-password")
                || path.equals("/auth-service/api/auth/reset-password")
                || path.equals("/auth-service/api/auth/verify-email");
    }

    /**
     * Extract JWT token from cookie or Authorization header
     */
    private String extractToken(ServerWebExchange exchange) {

        // Cookie-based token (optional)
        if (exchange.getRequest().getCookies().getFirst("JoJosCookie") != null) {
            return exchange.getRequest().getCookies()
                    .getFirst("JoJosCookie")
                    .getValue();
        }

        // Authorization header token
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}
