package com.app.booking.controller;

import com.app.booking.dto.response.*;
import com.app.booking.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/bookings-by-status")
    public ResponseEntity<List<BookingStatusReportResponse>> bookingsByStatus(
            Authentication auth) {

        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(reportService.getBookingsByStatus());
    }

    @GetMapping("/technician-task-count")
    public ResponseEntity<List<TechnicianTaskCountResponse>> technicianTaskCount(
            Authentication auth) {

        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(reportService.getTechnicianTaskCount());
    }

    // 🔒 Centralized role check
    private boolean isAdmin(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }
}
