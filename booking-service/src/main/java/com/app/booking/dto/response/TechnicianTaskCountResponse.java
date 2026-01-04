package com.app.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TechnicianTaskCountResponse {
    private String technicianId;
    private long taskCount;
}
