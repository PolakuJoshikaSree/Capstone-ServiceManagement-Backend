package com.app.booking.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.DirectExchange;

import static org.junit.jupiter.api.Assertions.*;

class RabbitConfigTest {

    private final RabbitConfig config = new RabbitConfig();

    @Test
    void exchangeBean_created() {
        DirectExchange exchange = config.exchange();
        assertEquals(RabbitConfig.EXCHANGE, exchange.getName());
    }

    @Test
    void messageConverter_created() {
        assertNotNull(config.messageConverter());
    }
}
