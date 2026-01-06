package com.app.notification.messaging;

import com.app.notification.dto.CreateNotificationRequest;
import com.app.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NotificationListenerTest {

    @Test
    void handleNotification_callsNotificationService() {

        // Arrange
        NotificationService notificationService =
                Mockito.mock(NotificationService.class);

        NotificationListener listener =
                new NotificationListener(notificationService);

        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setUserId("U1");
        request.setMessage("Test message");
        request.setType("TEST");

        // Assert
        Mockito.verify(notificationService)
                .create(request);
    }
}
