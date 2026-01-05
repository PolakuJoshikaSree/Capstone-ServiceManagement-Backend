package com.app.billing.messaging;

import com.app.billing.dto.notification.CreateNotificationRequest;
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
