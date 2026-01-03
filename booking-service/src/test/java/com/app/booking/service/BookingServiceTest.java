package com.app.booking.service;

import com.app.booking.client.BillingClient;
import com.app.booking.dto.billing.CreateInvoiceRequest;
import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.exception.BookingNotFoundException;
import com.app.booking.model.Booking;
import com.app.booking.model.BookingStatus;
import com.app.booking.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BillingClient billingClient;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_success() {

        CreateBookingRequest request = CreateBookingRequest.builder()
                .serviceName("Plumbing")
                .categoryName("Repair")
                .scheduledDate(LocalDate.now())
                .timeSlot("10:00 - 12:00")
                .address("Hyderabad")
                .issueDescription("Leak")
                .paymentMode("CASH")
                .build();

        BookingResponse response =
                bookingService.createBooking(request, "CUST1");

        assertNotNull(response.getBookingId());
        assertEquals("REQUESTED", response.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void assignTechnician_success() {

        Booking booking = Booking.builder()
                .bookingId("BK1")
                .status(BookingStatus.REQUESTED)
                .build();

        when(bookingRepository.findByBookingId("BK1"))
                .thenReturn(Optional.of(booking));

        BookingResponse response =
                bookingService.assignTechnician("BK1", "TECH1");

        assertEquals("ASSIGNED", response.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void assignTechnician_invalidStatus() {

        Booking booking = Booking.builder()
                .bookingId("BK1")
                .status(BookingStatus.CANCELLED)
                .build();

        when(bookingRepository.findByBookingId("BK1"))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.assignTechnician("BK1", "TECH1"));
    }

    @Test
    void updateStatus_completed_callsBillingService() {

        Booking booking = Booking.builder()
                .bookingId("BK1")
                .customerId("C1")
                .serviceName("Plumbing")
                .status(BookingStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .build();

        when(bookingRepository.findByBookingId("BK1"))
                .thenReturn(Optional.of(booking));

        BookingResponse response =
                bookingService.updateStatus("BK1", "COMPLETED");

        assertEquals("COMPLETED", response.getStatus());
        verify(billingClient).createInvoice(any(CreateInvoiceRequest.class));
    }

    @Test
    void updateStatus_bookingNotFound() {
        when(bookingRepository.findByBookingId("BAD"))
                .thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateStatus("BAD", "COMPLETED"));
    }
}
