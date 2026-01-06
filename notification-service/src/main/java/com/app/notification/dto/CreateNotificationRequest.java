package com.app.notification.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {

    private String userId;
    private String message;
    private String type;
}
