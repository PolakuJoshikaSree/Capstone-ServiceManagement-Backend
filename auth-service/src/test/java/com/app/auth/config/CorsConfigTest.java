package com.app.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.filter.CorsFilter;

import com.app.auth.config.CorsConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CorsConfigTest {

    @Test
    void corsFilterBeanCreated() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(CorsConfig.class);

        CorsFilter corsFilter = context.getBean(CorsFilter.class);

        assertNotNull(corsFilter);

        context.close();
    }
}
