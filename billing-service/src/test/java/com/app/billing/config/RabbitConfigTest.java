package com.app.billing.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RabbitConfigTest {

    RabbitConfig config = new RabbitConfig();

    @Test
    void exchangeBean_created() {
        assertNotNull(config.exchange());
    }

    @Test
    void queueBean_created() {
        assertNotNull(config.queue());
    }

    @Test
    void binding_created() {
        assertNotNull(
                config.binding(config.queue(), config.exchange())
        );
    }

    @Test
    void messageConverter_created() {
        assertNotNull(config.messageConverter());
    }
}
