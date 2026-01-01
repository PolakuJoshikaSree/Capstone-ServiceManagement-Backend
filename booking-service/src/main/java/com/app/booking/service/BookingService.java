package com.app.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.request.RescheduleBookingRequest;
import com.app.booking.dto.response.*;
import com.app.booking.exception.BookingNotFoundException;
import com.app.booking.model.Booking;
import com.app.booking.model.BookingStatus;
import com.app.booking.model.InvoiceLineItem;
import com.app.booking.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TechnicianService technicianService;
    private final InvoiceService invoiceService;

    /* ================= CREATE BOOKING ================= */

    public BookingResponse createBooking(
            CreateBookingRequest request,
            String customerId) {

        Booking booking = Booking.builder()
                .bookingId("BK-" + UUID.randomUUID().toString().substring(0, 8))
                .customerId(customerId)
                .serviceName(request.getServiceName())
                .categoryName(request.getCategoryName())
                .scheduledDate(request.getScheduledDate())
                .timeSlot(request.getTimeSlot())
                .address(request.getAddress())
                .issueDescription(request.getIssueDescription())
                .paymentMode(request.getPaymentMode())
                .status(BookingStatus.REQUESTED)
                .createdAt(LocalDateTime.now())
                .build();

        bookingRepository.save(booking);

        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .status(booking.getStatus().name())
                .build();
    }

    /* ================= READ ================= */

    public List<BookingListResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::toListResponse)
                .toList();
    }

    public BookingDetailsResponse getBookingByBookingId(String bookingId) {

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        return BookingDetailsResponse.builder()
                .bookingId(booking.getBookingId())
                .customerId(booking.getCustomerId())
                .serviceName(booking.getServiceName())
                .categoryName(booking.getCategoryName())
                .scheduledDate(booking.getScheduledDate())
                .timeSlot(booking.getTimeSlot())
                .address(booking.getAddress())
                .issueDescription(booking.getIssueDescription())
                .paymentMode(booking.getPaymentMode())
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    public List<BookingListResponse> getMyBookings(String customerId) {
        return bookingRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toListResponse)
                .toList();
    }

    public List<BookingListResponse> getBookingHistory() {
        return bookingRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toListResponse)
                .toList();
    }

    public List<BookingListResponse> getAssignedBookingsForTechnician(
            String technicianId) {

        return bookingRepository.findByTechnicianId(technicianId)
                .stream()
                .map(this::toListResponse)
                .toList();
    }

    /* ================= UPDATE ================= */

    public RescheduleBookingResponse rescheduleBooking(
            String bookingId,
            RescheduleBookingRequest request) {

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return RescheduleBookingResponse.builder()
                    .bookingId(bookingId)
                    .status("CANCELLED")
                    .message("Cancelled booking cannot be rescheduled")
                    .build();
        }

        booking.setScheduledDate(request.getScheduledDate());
        booking.setTimeSlot(request.getTimeSlot());
        bookingRepository.save(booking);

        return RescheduleBookingResponse.builder()
                .bookingId(bookingId)
                .status(booking.getStatus().name())
                .message("Booking has been rescheduled")
                .build();
    }

    public CancelBookingResponse cancelBooking(String bookingId) {

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return CancelBookingResponse.builder()
                    .bookingId(bookingId)
                    .status("CANCELLED")
                    .message("Booking is already cancelled")
                    .build();
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return CancelBookingResponse.builder()
                .bookingId(bookingId)
                .status("CANCELLED")
                .message("Booking cancelled")
                .build();
    }

    /* ================= ASSIGN TECHNICIAN ================= */

    public BookingResponse assignTechnician(
            String bookingId,
            String technicianId) {

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getStatus() != BookingStatus.REQUESTED) {
            throw new IllegalArgumentException(
                    "Only REQUESTED bookings can be assigned");
        }

        booking.setTechnicianId(technicianId);
        booking.setStatus(BookingStatus.ASSIGNED);
        bookingRepository.save(booking);

        technicianService.markBusy(technicianId);

        return BookingResponse.builder()
                .bookingId(bookingId)
                .status(booking.getStatus().name())
                .build();
    }

    /* ================= UPDATE STATUS ================= */

    public BookingResponse updateStatus(
            String bookingId,
            String status) {

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        BookingStatus newStatus = BookingStatus.valueOf(status);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cancelled booking cannot be updated");
        }

        booking.setStatus(newStatus);
        bookingRepository.save(booking);

        // -------- COMPLETION FLOW --------
        if (newStatus == BookingStatus.COMPLETED) {

            if (booking.getTechnicianId() != null) {
                technicianService.markAvailable(
                        booking.getTechnicianId());
            }

            // Generate invoice ONLY ONCE
            invoiceService.generateInvoiceIfAbsent(
                    booking.getBookingId(),
                    booking.getCustomerId(),
                    List.of(
                            InvoiceLineItem.builder()
                                    .description(booking.getServiceName())
                                    .unitPrice(499.0)
                                    .quantity(1)
                                    .build()
                    )
            );
        }

        return BookingResponse.builder()
                .bookingId(bookingId)
                .status(newStatus.name())
                .build();
    }

    /* ================= PAGINATION ================= */

    public Page<BookingListResponse> getAllBookingsPaged(
            Pageable pageable) {

        return bookingRepository.findAll(pageable)
                .map(this::toListResponse);
    }

    public Page<BookingListResponse> getMyBookingsPaged(
            String customerId,
            Pageable pageable) {

        return bookingRepository
                .findByCustomerId(customerId, pageable)
                .map(this::toListResponse);
    }

    /* ================= MAPPER ================= */

    private BookingListResponse toListResponse(Booking booking) {

        return BookingListResponse.builder()
                .bookingId(booking.getBookingId())
                .customerId(booking.getCustomerId())
                .serviceName(booking.getServiceName())
                .categoryName(booking.getCategoryName())
                .scheduledDate(booking.getScheduledDate())
                .timeSlot(booking.getTimeSlot())
                .address(booking.getAddress())
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
