package com.app.booking.messaging;

import com.app.booking.dto.CreateNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void send(CreateNotificationRequest request) {
        rabbitTemplate.convertAndSend(
                "notification.exchange",
                "notification.key",
                request
        );
    }
}
