package com.app.notification.messaging;

import com.app.notification.event.BookingCompletedEvent;
import com.app.notification.dto.CreateNotificationRequest;
import com.app.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationListener notificationListener;

    @Test
    void handleNotification_callsNotificationService() {
        // Arrange
        BookingCompletedEvent event = BookingCompletedEvent.builder()
                .bookingId("BK1")
                .customerId("U1")
                .serviceName("AC Repair")
                .build();

        // Assert
        verify(notificationService, times(1))
                .create(CreateNotificationRequest.builder()
                        .userId("U1")
                        .message("Booking confirmed for AC Repair")
                        .build());
    }
}
