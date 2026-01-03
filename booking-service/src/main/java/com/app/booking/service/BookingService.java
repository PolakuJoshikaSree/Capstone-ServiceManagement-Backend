package com.app.booking.service;

import com.app.booking.client.BillingClient;
import com.app.booking.dto.billing.CreateInvoiceRequest;
import com.app.booking.dto.billing.InvoiceLineItem;
import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.exception.BookingNotFoundException;
import com.app.booking.model.Booking;
import com.app.booking.model.BookingStatus;
import com.app.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BillingClient billingClient;

    // ================= CREATE =================
    public BookingResponse createBooking(CreateBookingRequest request, String customerId) {

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

        return new BookingResponse(
                booking.getBookingId(),
                booking.getStatus().name()
        );
    }

    // ================= READ =================
    public List<BookingListResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::toListResponse)
                .toList();
    }

    public List<BookingListResponse> getMyBookings(String customerId) {
        return bookingRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toListResponse)
                .toList();
    }

    // ================= TECHNICIAN =================
    public List<BookingListResponse> getAssignedBookingsForTechnician(String technicianId) {
        return bookingRepository.findByTechnicianId(technicianId)
                .stream()
                .map(this::toListResponse)
                .toList();
    }

    // ================= ASSIGN TECHNICIAN =================
    public BookingResponse assignTechnician(String bookingId, String technicianId) {

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getStatus() != BookingStatus.REQUESTED) {
            throw new IllegalArgumentException(
                    "Only REQUESTED bookings can be assigned");
        }

        booking.setTechnicianId(technicianId);
        booking.setStatus(BookingStatus.ASSIGNED);
        bookingRepository.save(booking);

        return new BookingResponse(
                booking.getBookingId(),
                booking.getStatus().name()
        );
    }

    // ================= UPDATE STATUS =================
    public BookingResponse updateStatus(String bookingId, String status) {

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        BookingStatus newStatus = BookingStatus.valueOf(status);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cancelled booking cannot be updated");
        }

        booking.setStatus(newStatus);
        bookingRepository.save(booking);

        // ===== COMPLETION FLOW =====
        if (newStatus == BookingStatus.COMPLETED) {

            billingClient.createInvoice(
                    CreateInvoiceRequest.builder()
                            .bookingId(booking.getBookingId())
                            .customerId(booking.getCustomerId())
                            .items(List.of(
                                    InvoiceLineItem.builder()
                                            .description(booking.getServiceName())
                                            .unitPrice(499.0)
                                            .quantity(1)
                                            .build()
                            ))
                            .build()
            );
        }

        return new BookingResponse(
                booking.getBookingId(),
                booking.getStatus().name()
        );
    }

    // ================= PAGINATION =================
    public Page<BookingListResponse> getAllBookingsPaged(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(this::toListResponse);
    }

    public Page<BookingListResponse> getMyBookingsPaged(String customerId, Pageable pageable) {
        return bookingRepository.findByCustomerId(customerId, pageable)
                .map(this::toListResponse);
    }

    // ================= MAPPER =================
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
