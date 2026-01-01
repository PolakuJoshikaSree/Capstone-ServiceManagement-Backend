package com.app.service_catalog.config;

import com.app.service_catalog.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // -------- PUBLIC (NO LOGIN REQUIRED) --------
                .requestMatchers(
                    "/api/services/**",
                    "/api/services/categories/**"
                ).permitAll()

                // -------- EVERYTHING ELSE --------
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
