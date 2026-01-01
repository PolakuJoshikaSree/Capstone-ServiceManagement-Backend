package com.app.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TechnicianWorkloadResponse {
    private String technicianId;
    private long totalBookings;
}
