package com.app.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.junit.jupiter.api.Assertions.*;

public class GatewaySecurityConfigTest {

    @Test
    void securityWebFilterChain_created() {
        GatewaySecurityConfig config = new GatewaySecurityConfig();
        SecurityWebFilterChain chain =
                config.securityWebFilterChain(
                        org.springframework.security.config.web.server.ServerHttpSecurity.http()
                );

        assertNotNull(chain);
    }
}
