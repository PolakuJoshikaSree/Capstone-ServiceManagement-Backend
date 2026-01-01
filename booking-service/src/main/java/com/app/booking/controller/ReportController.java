package com.app.booking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.app.booking.dto.response.BookingStatusReportResponse;
import com.app.booking.dto.response.TechnicianWorkloadResponse;

import com.app.booking.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/bookings-by-status")
    public List<BookingStatusReportResponse> bookingsByStatus() {
        return reportService.getBookingsByStatus();
    }

    @GetMapping("/technician-workload")
    public List<TechnicianWorkloadResponse> technicianWorkload() {
        return reportService.getTechnicianWorkload();
    }
}
