package com.app.booking.controller;

import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // ================= CREATE BOOKING =================
    @PostMapping
    public ResponseEntity<BookingResponse> create(
            Authentication authentication,
            @Valid @RequestBody CreateBookingRequest request
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String customerId = authentication.getName();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request, customerId));
    }

    // ================= MANAGER / ADMIN =================
    @GetMapping
    public ResponseEntity<List<BookingListResponse>> getAll(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // ================= CUSTOMER =================
    @GetMapping("/my")
    public ResponseEntity<List<BookingListResponse>> myBookings(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("Unauthorized");
        }
        return ResponseEntity.ok(
                bookingService.getMyBookings(auth.getName())
        );
    }

    // ================= TECHNICIAN =================
    @GetMapping("/technician/my")
    public ResponseEntity<List<BookingListResponse>> myAssignedBookings(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("Unauthorized");
        }
        return ResponseEntity.ok(
                bookingService.getAssignedBookingsForTechnician(auth.getName())
        );
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingResponse> updateStatus(
            Authentication auth,
            @PathVariable String bookingId,
            @RequestBody java.util.Map<String, String> body
    ) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("Unauthorized");
        }

        String status = body.get("status");
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }

        return ResponseEntity.ok(
                bookingService.updateStatus(bookingId, status)
        );
    }

    // ================= MANAGER: ASSIGN TECHNICIAN =================
    @PutMapping("/{bookingId}/assign/{technicianId}")
    public ResponseEntity<BookingResponse> assignTechnician(
            Authentication auth,
            @PathVariable String bookingId,
            @PathVariable String technicianId
    ) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("Unauthorized");
        }

        return ResponseEntity.ok(
                bookingService.assignTechnician(bookingId, technicianId)
        );
    }
}
