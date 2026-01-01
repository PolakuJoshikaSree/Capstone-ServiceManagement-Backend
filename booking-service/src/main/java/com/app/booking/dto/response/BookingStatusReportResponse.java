package com.app.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingStatusReportResponse {
    private String status;
    private long count;
}
