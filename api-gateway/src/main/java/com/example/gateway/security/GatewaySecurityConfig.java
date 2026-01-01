package com.example.gateway.security;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewaySecurityConfig {

    private final JwtAuthFilter jwtFilter;

    public GatewaySecurityConfig(JwtAuthFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex

                // OPTIONS (CORS)
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ---------------- PUBLIC AUTH ----------------
                .pathMatchers(
                    "/auth-service/api/auth/register",
                    "/auth-service/api/auth/login",
                    "/auth-service/api/auth/forgot-password",
                    "/auth-service/api/auth/reset-password",
                    "/auth-service/api/auth/verify-email"
                ).permitAll()

                // ---------------- PUBLIC TECHNICIAN REGISTER ----------------
                .pathMatchers(HttpMethod.POST, "/api/technicians/register").permitAll()

                // ---------------- EVERYTHING ELSE ----------------
                .anyExchange().authenticated()
            )
            .addFilterAfter(jwtFilter, SecurityWebFiltersOrder.CORS)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .build();
    }

    // CORS stays as-is (your config is correct)
}
