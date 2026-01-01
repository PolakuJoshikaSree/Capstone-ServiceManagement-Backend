package com.app.booking.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescheduleBookingResponse {

    private String bookingId;
    private String status;
    private String message;
}
