package com.app.notification.messaging;

import com.app.notification.event.BookingCancelledEvent;
import com.app.notification.event.BookingCompletedEvent;
import com.app.notification.dto.CreateNotificationRequest;
import com.app.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "notification.queue")
    public void handleBookingCompleted(BookingCompletedEvent event) {

        log.info("Booking completed {}", event.getBookingId());

        notificationService.create(
            CreateNotificationRequest.builder()
                .userId(event.getCustomerId())
                .role("CUSTOMER")
                .title("Service Completed")
                .message("Your service " + event.getServiceName() + " has been completed")
                .type("BOOKING_COMPLETED")
                .build()
        );
    }

    @RabbitListener(queues = "notification.queue")
    public void handleBookingCancelled(BookingCancelledEvent event) {

        log.info("Booking cancelled {}", event.getBookingId());

        notificationService.create(
            CreateNotificationRequest.builder()
                .userId(event.getCustomerId())
                .role("CUSTOMER")
                .title("Booking Cancelled")
                .message("Your booking for " + event.getServiceName() + " was cancelled")
                .type("BOOKING_CANCELLED")
                .build()
        );
    }
}
