package com.app.booking.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescheduleBookingRequest {

	@NotNull(message = "Scheduled date is required")
    @FutureOrPresent(message = "Scheduled date cannot be in the past")
    private LocalDate scheduledDate;

    @NotBlank(message = "Time slot is required")
    @Pattern(
        regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]\\s*-\\s*([0-1]?[0-9]|2[0-3]):[0-5][0-9]$",
        message = "Time slot must be in format 'HH:MM - HH:MM' (e.g., '10:00 - 12:00')"
    )
    private String timeSlot;
}
