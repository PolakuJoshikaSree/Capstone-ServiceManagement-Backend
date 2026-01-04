package com.app.booking.controller;

import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request, authentication.getName()));
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(
                bookingService.getMyBookings(auth.getName())
        );
    }

    // ================= TECHNICIAN =================
    @GetMapping("/technician/my")
    public ResponseEntity<List<BookingListResponse>> myAssignedBookings(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(
                bookingService.getAssignedBookingsForTechnician(auth.getName())
        );
    }

    // ================= UPDATE STATUS (FIXED) =================
    @PutMapping("/{bookingId}/status")
    public ResponseEntity<?> updateStatus(
            Authentication auth,
            @PathVariable String bookingId,
            @RequestBody Map<String, String> body
    ) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized");
        }

        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Status is required");
        }

        try {
            BookingResponse response =
                    bookingService.updateStatus(bookingId, status);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // ================= MANAGER: ASSIGN TECHNICIAN =================
    @PutMapping("/{bookingId}/assign/{technicianId}")
    public ResponseEntity<?> assignTechnician(
            Authentication auth,
            @PathVariable String bookingId,
            @PathVariable String technicianId
    ) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized");
        }

        try {
            return ResponseEntity.ok(
                    bookingService.assignTechnician(bookingId, technicianId)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
