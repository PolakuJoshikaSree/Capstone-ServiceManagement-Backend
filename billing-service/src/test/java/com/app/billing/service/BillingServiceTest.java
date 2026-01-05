package com.app.billing.service;

import com.app.billing.dto.CreateInvoiceRequest;
import com.app.billing.dto.InvoiceLineItemDto;
import com.app.billing.dto.MonthlyRevenueDTO;
import com.app.billing.messaging.NotificationPublisher;
import com.app.billing.model.Invoice;
import com.app.billing.model.InvoiceStatus;
import com.app.billing.model.PaymentStatus;
import com.app.billing.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BillingServiceTest {

    private final InvoiceRepository invoiceRepository =
            Mockito.mock(InvoiceRepository.class);

    private final NotificationPublisher notificationPublisher =
            Mockito.mock(NotificationPublisher.class);

    private final BillingService billingService =
            new BillingService(invoiceRepository, notificationPublisher);

    /* ================= CREATE INVOICE ================= */

    @Test
    void createInvoice_success_andNotificationSent() {

        CreateInvoiceRequest request = CreateInvoiceRequest.builder()
                .bookingId("BK1")
                .customerId("C1")
                .items(List.of(
                        InvoiceLineItemDto.builder()
                                .description("AC Service")
                                .unitPrice(500)
                                .quantity(1)
                                .build()
                ))
                .build();

        Mockito.when(invoiceRepository.findByBookingId("BK1"))
                .thenReturn(Optional.empty());

        billingService.createInvoice(request);

        Mockito.verify(invoiceRepository)
                .save(Mockito.any(Invoice.class));

        Mockito.verify(notificationPublisher)
                .send(Mockito.any());
    }

    @Test
    void createInvoice_duplicate_booking_ignored() {

        Mockito.when(invoiceRepository.findByBookingId("BK1"))
                .thenReturn(Optional.of(new Invoice()));

        billingService.createInvoice(
                CreateInvoiceRequest.builder()
                        .bookingId("BK1")
                        .items(List.of())
                        .build()
        );

        Mockito.verify(invoiceRepository, Mockito.never())
                .save(Mockito.any());

        Mockito.verify(notificationPublisher, Mockito.never())
                .send(Mockito.any());
    }

    @Test
    void createInvoice_notificationFailure_doesNotBreakFlow() {

        CreateInvoiceRequest request = CreateInvoiceRequest.builder()
                .bookingId("BK2")
                .customerId("C2")
                .items(List.of(
                        InvoiceLineItemDto.builder()
                                .description("Cleaning")
                                .unitPrice(300)
                                .quantity(2)
                                .build()
                ))
                .build();

        Mockito.when(invoiceRepository.findByBookingId("BK2"))
                .thenReturn(Optional.empty());

        Mockito.doThrow(new RuntimeException("Notification down"))
                .when(notificationPublisher)
                .send(Mockito.any());

        billingService.createInvoice(request);

        Mockito.verify(invoiceRepository)
                .save(Mockito.any(Invoice.class));
    }

    /* ================= MARK PAID ================= */

    @Test
    void markPaid_success() {

        Invoice invoice = Invoice.builder()
                .bookingId("BK1")
                .invoiceStatus(InvoiceStatus.GENERATED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Mockito.when(invoiceRepository.findByBookingId("BK1"))
                .thenReturn(Optional.of(invoice));

        Mockito.when(invoiceRepository.save(Mockito.any()))
                .thenAnswer(i -> i.getArgument(0));

        Invoice result = billingService.markPaid("BK1");

        assertEquals(InvoiceStatus.PAID, result.getInvoiceStatus());
        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
    }

    @Test
    void markPaid_invoiceNotFound() {

        Mockito.when(invoiceRepository.findByBookingId("X"))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> billingService.markPaid("X")
        );
    }

    /* ================= READ ================= */

    @Test
    void getAllInvoices() {

        Mockito.when(invoiceRepository.findAll())
                .thenReturn(List.of(new Invoice()));

        assertEquals(1, billingService.getAllInvoices().size());
    }

    @Test
    void getInvoicesByCustomer() {

        Mockito.when(invoiceRepository.findByCustomerId("C1"))
                .thenReturn(List.of(new Invoice()));

        assertEquals(1, billingService.getInvoicesByCustomer("C1").size());
    }

    /* ================= REPORT ================= */

    @Test
    void getMonthlyRevenue_success() {

        Mockito.when(invoiceRepository.getMonthlyRevenue())
                .thenReturn(List.of(
                        new MonthlyRevenueDTO(2025, 1, 1000.0),
                        new MonthlyRevenueDTO(2025, 2, 2500.0)
                ));

        Map<String, Double> revenue = billingService.getMonthlyRevenue();

        assertEquals(2, revenue.size());
        assertEquals(1000.0, revenue.get("2025-01"));
        assertEquals(2500.0, revenue.get("2025-02"));
    }

    @Test
    void getMonthlyRevenue_emptyResult() {

        Mockito.when(invoiceRepository.getMonthlyRevenue())
                .thenReturn(List.of());

        Map<String, Double> revenue = billingService.getMonthlyRevenue();

        assertTrue(revenue.isEmpty());
    }

    @Test
    void getMonthlyRevenue_nullResult() {

        Mockito.when(invoiceRepository.getMonthlyRevenue())
                .thenReturn(null);

        Map<String, Double> revenue = billingService.getMonthlyRevenue();

        assertTrue(revenue.isEmpty());
    }
}
