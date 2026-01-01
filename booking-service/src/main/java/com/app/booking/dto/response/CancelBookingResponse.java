package com.app.booking.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelBookingResponse {

    private String bookingId;
    private String status;
    private String message;
}
