package com.app.notification.event;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCancelledEvent implements Serializable {

    private String bookingId;
    private String customerId;
    private String serviceName;
}
