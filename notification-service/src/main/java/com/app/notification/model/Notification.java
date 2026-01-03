package com.app.notification.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private String userId;     // customer or technician
    private String role;       // CUSTOMER / TECHNICIAN
    private String title;
    private String message;
    private String type;       // BOOKING_CREATED, ASSIGNED, COMPLETED

    private boolean read;
    private Instant createdAt;
}
