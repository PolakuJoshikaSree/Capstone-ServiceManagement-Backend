package com.app.booking.config;

import com.app.booking.security.AuthFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    @Test
    void securityFilterChain_created() throws Exception {
        AuthFilter filter = Mockito.mock(AuthFilter.class);
        SecurityConfig config = new SecurityConfig(filter);

        SecurityFilterChain chain =
                config.filterChain(Mockito.mock(org.springframework.security.config.annotation.web.builders.HttpSecurity.class, Mockito.RETURNS_DEEP_STUBS));

        assertNotNull(chain);
    }
}
