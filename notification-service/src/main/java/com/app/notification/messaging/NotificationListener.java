package com.app.notification.messaging;

import com.app.notification.dto.CreateNotificationRequest;
import com.app.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "notification.queue")
    public void handleNotification(CreateNotificationRequest request) {
        log.info("Notification received: {}", request);
        notificationService.create(request);
    }
}
