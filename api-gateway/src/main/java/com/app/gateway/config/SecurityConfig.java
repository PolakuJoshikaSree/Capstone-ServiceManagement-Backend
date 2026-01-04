package com.app.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(cors -> {}) // REQUIRED for Swagger UI

            .authorizeExchange(ex -> ex

                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()
                
                .pathMatchers(
                    "/auth-service/v3/api-docs",
                    "/booking-service/v3/api-docs",
                    "/billing-service/v3/api-docs",
                    "/service-catalog-service/v3/api-docs"
                ).permitAll()

                .pathMatchers(
                    "/auth-service/**"
                ).permitAll()

                .anyExchange().permitAll()
            )
            .build();
    }
}
