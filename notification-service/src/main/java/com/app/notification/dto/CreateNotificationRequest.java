package com.app.notification.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {

    private String userId;
    private String role;     // CUSTOMER / TECHNICIAN
    private String title;
    private String message;
    private String type;     // BOOKING_COMPLETED / BOOKING_CANCELLED
}
