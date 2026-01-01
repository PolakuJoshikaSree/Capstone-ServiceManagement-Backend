package com.app.booking.controller;

import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.request.RescheduleBookingRequest;
import com.app.booking.dto.response.*;
import com.app.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // ---------------- CREATE BOOKING (CUSTOMER) ----------------
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String customerId = auth.getName(); // JWT subject

        BookingResponse response =
                bookingService.createBooking(request, customerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ---------------- GET ALL BOOKINGS (ADMIN) ----------------
    @GetMapping
    public ResponseEntity<List<BookingListResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // ---------------- GET BOOKING BY ID ----------------
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> getBookingByBookingId(
            @PathVariable String bookingId) {

        return ResponseEntity.ok(
                bookingService.getBookingByBookingId(bookingId)
        );
    }

    // ---------------- MY BOOKINGS (CUSTOMER) ----------------
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingListResponse>> getMyBookings() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String customerId = auth.getName();

        return ResponseEntity.ok(
                bookingService.getMyBookings(customerId)
        );
    }

    // ---------------- BOOKING HISTORY (ADMIN) ----------------
    @GetMapping("/history")
    public ResponseEntity<List<BookingListResponse>> getBookingHistory() {
        return ResponseEntity.ok(
                bookingService.getBookingHistory()
        );
    }

    // ---------------- RESCHEDULE (CUSTOMER) ----------------
    @PutMapping("/{bookingId}/reschedule")
    public ResponseEntity<RescheduleBookingResponse> rescheduleBooking(
            @PathVariable String bookingId,
            @Valid @RequestBody RescheduleBookingRequest request) {

        return ResponseEntity.ok(
                bookingService.rescheduleBooking(bookingId, request)
        );
    }

    // ---------------- CANCEL (CUSTOMER) ----------------
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<CancelBookingResponse> cancelBooking(
            @PathVariable String bookingId) {

        return ResponseEntity.ok(
                bookingService.cancelBooking(bookingId)
        );
    }

    // ---------------- ASSIGNED BOOKINGS (TECHNICIAN) ----------------
    @GetMapping("/technician/assigned")
    public ResponseEntity<List<BookingListResponse>> getAssignedBookings() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String technicianId = auth.getName();

        return ResponseEntity.ok(
                bookingService.getAssignedBookingsForTechnician(technicianId)
        );
    }

    // ---------------- ASSIGN TECHNICIAN (MANAGER / ADMIN) ----------------
    @PutMapping("/{bookingId}/assign")
    public ResponseEntity<BookingResponse> assignTechnician(
            @PathVariable String bookingId,
            @RequestParam String technicianId) {

        return ResponseEntity.ok(
                bookingService.assignTechnician(bookingId, technicianId)
        );
    }

    // ---------------- UPDATE STATUS (TECHNICIAN) ----------------
    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable String bookingId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                bookingService.updateStatus(bookingId, status)
        );
    }
}
