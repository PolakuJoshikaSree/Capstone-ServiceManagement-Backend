package com.app.billing.event;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCompletedEvent  {

    private String bookingId;
    private String customerId;
    private String serviceName;
}
