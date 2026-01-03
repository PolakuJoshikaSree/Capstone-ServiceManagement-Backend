package com.app.notification.service;

import com.app.notification.dto.CreateNotificationRequest;
import com.app.notification.model.Notification;
import com.app.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public Notification create(CreateNotificationRequest request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .role(request.getRole())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .read(false)
                .createdAt(Instant.now())
                .build();

        return repository.save(notification);
    }

    public List<Notification> getForUser(String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(String id) {
        repository.findById(id).ifPresent(n -> {
            n.setRead(true);
            repository.save(n);
        });
    }
}
