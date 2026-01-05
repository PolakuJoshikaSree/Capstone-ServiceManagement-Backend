package com.app.billing.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNotificationRequest {

    private String userId;
    private String role;
    private String title;
    private String message;
    private String type;
}
