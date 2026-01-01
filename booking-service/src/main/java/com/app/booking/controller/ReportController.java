package com.app.booking.controller;

import com.app.booking.dto.response.*;
import com.app.booking.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/bookings-by-status")
    public List<BookingStatusReportResponse> bookingsByStatus(
            @RequestHeader("X-USER-ROLES") String roles) {

        if (!roles.contains("ROLE_ADMIN"))
            throw new SecurityException("Forbidden");

        return reportService.getBookingsByStatus();
    }
}
