package com.app.billing.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SecurityConfigTest {

    @Test
    void securityFilterChain_created() throws Exception {

        SecurityConfig config = new SecurityConfig();

        SecurityFilterChain chain =
                config.filterChain(Mockito.mock(
                        org.springframework.security.config.annotation.web.builders.HttpSecurity.class,
                        Mockito.RETURNS_DEEP_STUBS));

        assertNotNull(chain);
    }
}
