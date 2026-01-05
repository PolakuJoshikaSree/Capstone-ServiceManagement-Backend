package com.app.booking.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

class WebClientConfigTest {

    @Test
    void webClientBean_created() {
        WebClientConfig config = new WebClientConfig();
        WebClient client = config.webClient(WebClient.builder());
        assertNotNull(client);
    }
}
