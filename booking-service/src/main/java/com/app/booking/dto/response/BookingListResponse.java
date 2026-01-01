package com.app.booking.dto.response;

import lombok.*;

import java.time.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingListResponse {

    private String bookingId;
    private String customerId;

    private String serviceName;
    private String categoryName;

    private LocalDate scheduledDate;
    private String timeSlot;

    private String address;
    private String status;
    private LocalDateTime createdAt;
}
