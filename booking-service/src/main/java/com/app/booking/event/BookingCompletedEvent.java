package com.app.booking.event;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCompletedEvent implements Serializable {

    private String bookingId;
    private String customerId;
    private String serviceName;
}
