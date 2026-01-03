package com.app.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.junit.jupiter.api.Assertions.*;

public class CorsConfigTest {

    @Test
    void corsWebFilter_createdSuccessfully() {
        CorsConfig config = new CorsConfig();
        CorsWebFilter filter = config.corsWebFilter();
        assertNotNull(filter);
    }
}
