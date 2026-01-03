package com.app.notification.dto;

import lombok.Data;

@Data
public class CreateNotificationRequest {

    private String userId;
    private String role;
    private String title;
    private String message;
    private String type;
}
