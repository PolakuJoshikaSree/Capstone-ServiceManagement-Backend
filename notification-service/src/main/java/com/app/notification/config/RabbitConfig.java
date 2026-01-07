package com.app.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String BOOKING_EXCHANGE = "booking.exchange";
    public static final String QUEUE = "notification.queue";
    public static final String ROUTING_KEY = "booking.completed";
    public static final String CANCEL_ROUTING_KEY = "booking.cancelled";


    @Bean
    public DirectExchange bookingExchange() {
        return new DirectExchange(BOOKING_EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding bookingCompletedBinding(
            Queue notificationQueue,
            DirectExchange bookingExchange
    ) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(bookingExchange)
                .with(ROUTING_KEY);
    }
    @Bean
    public Binding bookingCancelledBinding(
            Queue notificationQueue,
            DirectExchange bookingExchange
    ) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(bookingExchange)
                .with(CANCEL_ROUTING_KEY);
    }

    
}

