package com.app.booking.service;

import com.app.booking.client.NotificationClient;
import com.app.booking.dto.CreateNotificationRequest;
import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
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
import static com.app.booking.config.RabbitConfig.ROUTING_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final NotificationClient notificationClient;
    private final RabbitTemplate rabbitTemplate;

    // ================= CREATE BOOKING =================
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

        notificationClient.sendNotification(
                CreateNotificationRequest.builder()
                        .userId(customerId)
                        .role("CUSTOMER")
                        .title("Booking Created")
                        .message("Your booking " + booking.getBookingId() + " was created successfully")
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

        BookingStatus newStatus = BookingStatus.valueOf(statusValue.toUpperCase());
        booking.setStatus(newStatus);
        bookingRepository.save(booking);

        if (newStatus == BookingStatus.COMPLETED) {

            log.info("Booking {} completed. Publishing event...", bookingId);

            BookingCompletedEvent event = BookingCompletedEvent.builder()
                    .bookingId(booking.getBookingId())
                    .customerId(booking.getCustomerId())
                    .serviceName(booking.getServiceName())
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);

            log.info("BookingCompletedEvent sent for {}", bookingId);
        }

        return new BookingResponse(
                booking.getBookingId(),
                booking.getStatus().name()
        );
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
