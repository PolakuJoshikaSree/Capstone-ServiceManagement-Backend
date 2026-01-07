package com.app.booking.event;

import java.io.Serializable;

import com.app.booking.event.BookingCancelledEvent;

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
