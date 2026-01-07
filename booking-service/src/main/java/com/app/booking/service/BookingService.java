package com.app.booking.service;

import com.app.booking.client.BillingClient;
import com.app.booking.client.NotificationClient;
import com.app.booking.dto.CreateNotificationRequest;
import com.app.booking.dto.billing.CreateInvoiceRequest;
import com.app.booking.dto.billing.InvoiceLineItem;
import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.request.RescheduleBookingRequest;
import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.dto.response.CancelBookingResponse;
import com.app.booking.dto.response.RescheduleBookingResponse;
import com.app.booking.event.BookingCancelledEvent;
import com.app.booking.event.BookingCompletedEvent;
import com.app.booking.exception.BookingNotFoundException;
import com.app.booking.model.Booking;
import com.app.booking.model.BookingStatus;
import com.app.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.app.booking.config.RabbitConfig.EXCHANGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final NotificationClient notificationClient;
    private final RabbitTemplate rabbitTemplate;
    private final BillingClient billingClient;

    // ================= CREATE BOOKING =================
    public BookingResponse createBooking(CreateBookingRequest request, String customerId) {

        double servicePrice =
                500 + (Math.random() * 1500);  

        // round to 2 decimals & ensure non-zero
        servicePrice = Math.max(100,
                Math.round(servicePrice * 100.0) / 100.0
        );

        log.info("💰 Generated price = ₹{} for service {}",
                servicePrice, request.getServiceName());

        Booking booking = Booking.builder()
                .bookingId("BK-" + UUID.randomUUID().toString().substring(0, 8))
                .customerId(customerId)
                .serviceName(request.getServiceName())
                .categoryName(request.getCategoryName())
                .servicePrice(servicePrice)   // 🔒 LOCKED
                .scheduledDate(request.getScheduledDate())
                .timeSlot(request.getTimeSlot())
                .address(request.getAddress())
                .issueDescription(request.getIssueDescription())
                .paymentMode(request.getPaymentMode())
                .status(BookingStatus.REQUESTED)
                .createdAt(LocalDateTime.now())
                .build();

        bookingRepository.save(booking);

        notificationClient.sendNotification(
                CreateNotificationRequest.builder()
                        .userId(customerId)
                        .role("CUSTOMER")
                        .title("Booking Created")
                        .message(
                            "Your booking " + booking.getBookingId()
                            + " was created. Price: ₹" + servicePrice
                        )
                        .type("BOOKING_CREATED")
                        .build()
        );

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

        booking.setTechnicianId(technicianId);
        booking.setStatus(BookingStatus.ASSIGNED);
        bookingRepository.save(booking);

        notificationClient.sendNotification(
                CreateNotificationRequest.builder()
                        .userId(technicianId)
                        .role("TECHNICIAN")
                        .title("New Service Assigned")
                        .message("You have been assigned booking " + booking.getBookingId())
                        .type("TECHNICIAN_ASSIGNED")
                        .build()
        );

        return new BookingResponse(
                booking.getBookingId(),
                booking.getStatus().name()
        );
    }

    // ================= UPDATE STATUS =================
    public BookingResponse updateStatus(String bookingId, String statusValue) {

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        BookingStatus newStatus;
        try {
            newStatus = BookingStatus.valueOf(statusValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid booking status");
        }

        BookingStatus currentStatus = booking.getStatus();

        if (currentStatus == BookingStatus.CANCELLED || currentStatus == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Booking cannot be updated");
        }

        boolean validTransition =
                (currentStatus == BookingStatus.ASSIGNED && newStatus == BookingStatus.IN_PROGRESS) ||
                (currentStatus == BookingStatus.IN_PROGRESS && newStatus == BookingStatus.COMPLETED);

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Invalid status transition: " + currentStatus + " → " + newStatus
            );
        }

        booking.setStatus(newStatus);
        bookingRepository.save(booking);

        // ================= COMPLETED =================
        if (newStatus == BookingStatus.COMPLETED) {

            rabbitTemplate.convertAndSend(
                    EXCHANGE,
                    "booking.completed",
                    BookingCompletedEvent.builder()
                            .bookingId(booking.getBookingId())
                            .customerId(booking.getCustomerId())
                            .serviceName(booking.getServiceName())
                            .build()
            );

            billingClient.createInvoice(
                    CreateInvoiceRequest.builder()
                            .bookingId(booking.getBookingId())
                            .customerId(booking.getCustomerId())
                            .items(List.of(
                                    new InvoiceLineItem(
                                            booking.getServiceName(),
                                            booking.getServicePrice(), // 🔥 SAME RANDOM PRICE
                                            1
                                    )
                            ))
                            .build()
            );

            notificationClient.sendNotification(
                    CreateNotificationRequest.builder()
                            .userId(booking.getTechnicianId())
                            .role("TECHNICIAN")
                            .title("Service Completed")
                            .message("You completed booking " + booking.getBookingId())
                            .type("SERVICE_COMPLETED")
                            .build()
            );
        }

        return new BookingResponse(
                booking.getBookingId(),
                booking.getStatus().name()
        );
    }

    // ================= CANCEL =================
    public CancelBookingResponse cancelBooking(String bookingId, String userId) {

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Completed bookings cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        rabbitTemplate.convertAndSend(
                EXCHANGE,
                "booking.cancelled",
                BookingCancelledEvent.builder()
                        .bookingId(booking.getBookingId())
                        .customerId(booking.getCustomerId())
                        .serviceName(booking.getServiceName())
                        .build()
        );

        return CancelBookingResponse.builder()
                .bookingId(bookingId)
                .status("CANCELLED")
                .message("Booking cancelled successfully")
                .build();
    }

    // ================= RESCHEDULE =================
    public RescheduleBookingResponse rescheduleBooking(
            String bookingId,
            RescheduleBookingRequest request
    ) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getStatus() == BookingStatus.COMPLETED
                || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking cannot be rescheduled");
        }

        booking.setScheduledDate(request.getScheduledDate());
        booking.setTimeSlot(request.getTimeSlot());
        bookingRepository.save(booking);

        return RescheduleBookingResponse.builder()
                .bookingId(bookingId)
                .status(booking.getStatus().name())
                .message("Booking rescheduled successfully")
                .build();
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
