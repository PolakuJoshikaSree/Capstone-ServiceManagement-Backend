package com.app.booking.service;

import com.app.booking.client.BillingClient;
import com.app.booking.client.NotificationClient;
import com.app.booking.dto.billing.CreateInvoiceRequest;
import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.event.BookingCompletedEvent;
import com.app.booking.exception.BookingNotFoundException;
import com.app.booking.model.Booking;
import com.app.booking.model.BookingStatus;
import com.app.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    private BookingRepository bookingRepository;
    private NotificationClient notificationClient;
    private RabbitTemplate rabbitTemplate;
    private BillingClient billingClient;

    private BookingService service;

    @BeforeEach
    void setup() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        notificationClient = Mockito.mock(NotificationClient.class);
        rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        billingClient = Mockito.mock(BillingClient.class);
    }

    // ---------- CREATE BOOKING ----------
    @Test
    void createBooking_success() {

        CreateBookingRequest request = CreateBookingRequest.builder()
                .serviceName("Plumbing")
                .categoryName("Home")
                .scheduledDate(LocalDate.now())
                .timeSlot("10:00 - 12:00")
                .address("Addr")
                .issueDescription("Leak")
                .paymentMode("CASH")
                .build();

        Mockito.when(bookingRepository.save(Mockito.any()))
                .thenAnswer(i -> i.getArgument(0));

        var response = service.createBooking(request, "CUST1");

        assertNotNull(response.getBookingId());
        assertEquals("REQUESTED", response.getStatus());

        Mockito.verify(notificationClient)
                .sendNotification(Mockito.any());
    }

    // ---------- READ ----------
    @Test
    void getAllBookings_returnsList() {

        Mockito.when(bookingRepository.findAll())
                .thenReturn(List.of(
                        Booking.builder()
                                .bookingId("BK1")
                                .status(BookingStatus.REQUESTED)
                                .build()
                ));

        assertEquals(1, service.getAllBookings().size());
    }

    @Test
    void getMyBookings_returnsList() {

        Mockito.when(bookingRepository.findByCustomerId("C1"))
                .thenReturn(List.of(
                        Booking.builder()
                                .bookingId("BK1")
                                .customerId("C1")
                                .status(BookingStatus.REQUESTED)
                                .build()
                ));

        assertEquals(1, service.getMyBookings("C1").size());
    }

    @Test
    void getAssignedBookingsForTechnician_returnsList() {

        Mockito.when(bookingRepository.findByTechnicianId("T1"))
                .thenReturn(List.of(
                        Booking.builder()
                                .bookingId("BK1")
                                .technicianId("T1")
                                .status(BookingStatus.ASSIGNED)
                                .build()
                ));

        assertEquals(1, service.getAssignedBookingsForTechnician("T1").size());
    }

    // ---------- ASSIGN TECHNICIAN ----------
    @Test
    void assignTechnician_success() {

        Booking booking = Booking.builder()
                .bookingId("BK1")
                .status(BookingStatus.REQUESTED)
                .build();

        Mockito.when(bookingRepository.findByBookingId("BK1"))
                .thenReturn(Optional.of(booking));

        var response = service.assignTechnician("BK1", "TECH1");

        assertEquals("ASSIGNED", response.getStatus());

        Mockito.verify(notificationClient)
                .sendNotification(Mockito.any());
    }

    @Test
    void assignTechnician_notFound() {

        Mockito.when(bookingRepository.findByBookingId("X"))
                .thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class,
                () -> service.assignTechnician("X", "T1")
        );
    }

    // ---------- UPDATE STATUS (NON-COMPLETED) ----------
    @Test
    void updateStatus_toInProgress_noBilling() {

        Booking booking = Booking.builder()
                .bookingId("BK1")
                .status(BookingStatus.ASSIGNED)
                .build();

        Mockito.when(bookingRepository.findByBookingId("BK1"))
                .thenReturn(Optional.of(booking));

        var response = service.updateStatus("BK1", "IN_PROGRESS");

        assertEquals("IN_PROGRESS", response.getStatus());

        Mockito.verify(billingClient, Mockito.never())
                .createInvoice(Mockito.any());
    }

    // ---------- UPDATE STATUS (COMPLETED) ----------
    @Test
    void updateStatus_completed_triggersEvent_notification_and_billing() {

        Booking booking = Booking.builder()
                .bookingId("BK1")
                .customerId("C1")
                .technicianId("T1")
                .serviceName("AC Repair")
                .servicePrice(1200)
                .status(BookingStatus.IN_PROGRESS)
                .build();

        Mockito.when(bookingRepository.findByBookingId("BK1"))
                .thenReturn(Optional.of(booking));

        var response = service.updateStatus("BK1", "COMPLETED");

        assertEquals("COMPLETED", response.getStatus());

        // RabbitMQ event
        ArgumentCaptor<BookingCompletedEvent> eventCaptor =
                ArgumentCaptor.forClass(BookingCompletedEvent.class);

        Mockito.verify(rabbitTemplate)
                .convertAndSend(Mockito.any(), Mockito.any(), eventCaptor.capture());

        assertEquals("BK1", eventCaptor.getValue().getBookingId());

        // Notification
        Mockito.verify(notificationClient)
                .sendNotification(Mockito.any());

        // 🔥 Billing invoice
        ArgumentCaptor<CreateInvoiceRequest> invoiceCaptor =
                ArgumentCaptor.forClass(CreateInvoiceRequest.class);

        Mockito.verify(billingClient)
                .createInvoice(invoiceCaptor.capture());

        assertEquals("BK1", invoiceCaptor.getValue().getBookingId());
        assertEquals("C1", invoiceCaptor.getValue().getCustomerId());
        assertEquals(1200, invoiceCaptor.getValue().getItems().get(0).getUnitPrice());
    }

    @Test
    void updateStatus_bookingNotFound() {

        Mockito.when(bookingRepository.findByBookingId("X"))
                .thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class,
                () -> service.updateStatus("X", "COMPLETED")
        );
    }
}
