package com.app.booking.controller;

import com.app.booking.dto.request.*;
import com.app.booking.dto.response.*;
import com.app.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(
            @RequestHeader("X-USER-ID") String customerId,
            @Valid @RequestBody CreateBookingRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request, customerId));
    }

    @GetMapping
    public List<BookingListResponse> getAll(
            @RequestHeader("X-USER-ROLES") String roles) {

        if (!roles.contains("ROLE_ADMIN"))
            throw new SecurityException("Forbidden");

        return bookingService.getAllBookings();
    }

    @GetMapping("/my-bookings")
    public List<BookingListResponse> myBookings(
            @RequestHeader("X-USER-ID") String userId) {

        return bookingService.getMyBookings(userId);
    }

    @PutMapping("/{bookingId}/assign")
    public BookingResponse assign(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String bookingId,
            @RequestParam String technicianId) {

        if (!roles.contains("ROLE_ADMIN") && !roles.contains("ROLE_MANAGER"))
            throw new SecurityException("Forbidden");

        return bookingService.assignTechnician(bookingId, technicianId);
    }

    @PutMapping("/{bookingId}/status")
    public BookingResponse updateStatus(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String bookingId,
            @RequestParam String status) {

        if (!roles.contains("ROLE_TECHNICIAN"))
            throw new SecurityException("Forbidden");

        return bookingService.updateStatus(bookingId, status);
    }@GetMapping("/paged")
    public Page<BookingListResponse> getAllPaged(
            @RequestHeader("X-USER-ROLES") String roles,
            Pageable pageable) {

        if (!roles.contains("ROLE_ADMIN")) {
            throw new SecurityException("Forbidden");
        }

        return bookingService.getAllBookingsPaged(pageable);
    }

    @GetMapping("/my-bookings/paged")
    public Page<BookingListResponse> getMyBookingsPaged(
            @RequestHeader("X-USER-ID") String userId,
            Pageable pageable) {

        return bookingService.getMyBookingsPaged(userId, pageable);
    }

}
