package com.app.notification.service;

import com.app.notification.dto.CreateNotificationRequest;
import com.app.notification.model.Notification;
import com.app.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService service;

    @Test
    void createNotification_shouldSaveNotification() {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setUserId("U1");
        request.setMessage("Test message");
        request.setType("BOOKING_CREATED");

        when(repository.save(any(Notification.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Notification result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.isRead()).isFalse();
        assertThat(result.getUserId()).isEqualTo("U1");

        verify(repository, times(1)).save(any(Notification.class));
    }

    @Test
    void getForUser_shouldReturnNotifications() {
        when(repository.findByUserIdOrderByCreatedAtDesc("U1"))
                .thenReturn(List.of(new Notification()));

        List<Notification> result = service.getForUser("U1");

        assertThat(result).hasSize(1);
        verify(repository).findByUserIdOrderByCreatedAtDesc("U1");
    }

    @Test
    void markAsRead_shouldUpdateNotification() {
        Notification n = Notification.builder().read(false).build();

        when(repository.findById("1")).thenReturn(Optional.of(n));

        service.markAsRead("1");

        assertThat(n.isRead()).isTrue();
        verify(repository).save(n);
    }

    @Test
    void markAsRead_shouldDoNothingIfNotFound() {
        when(repository.findById("99")).thenReturn(Optional.empty());

        service.markAsRead("99");

        verify(repository, never()).save(any());
    }
}
