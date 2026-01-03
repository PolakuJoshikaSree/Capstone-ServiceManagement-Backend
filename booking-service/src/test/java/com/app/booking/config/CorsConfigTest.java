package com.app.booking.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.filter.CorsFilter;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    @Test
    void corsFilter_created() {
        CorsConfig config = new CorsConfig();
        CorsFilter filter = config.corsFilter();

        assertNotNull(filter);
    }
}
