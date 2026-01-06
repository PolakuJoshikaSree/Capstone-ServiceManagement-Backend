package com.app.notification.event;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCompletedEvent implements Serializable {

    private String bookingId;
    private String customerId;
    private String serviceName;
}
