package com.app.notification.controller;

import com.app.notification.dto.CreateNotificationRequest;
import com.app.notification.model.Notification;
import com.app.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public Notification create(@RequestBody CreateNotificationRequest request) {
        return service.create(request);
    }

    @GetMapping("/user/{userId}")
    public List<Notification> getForUser(@PathVariable String userId) {
        return service.getForUser(userId);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable String id) {
        service.markAsRead(id);
    }
}
