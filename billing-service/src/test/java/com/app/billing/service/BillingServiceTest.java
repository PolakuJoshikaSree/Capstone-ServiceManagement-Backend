package com.app.billing.service;

import com.app.billing.dto.CreateInvoiceRequest;
import com.app.billing.dto.InvoiceLineItemDto;
import com.app.billing.model.Invoice;
import com.app.billing.model.InvoiceStatus;
import com.app.billing.model.PaymentStatus;
import com.app.billing.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private BillingService billingService;

    @Test
    void createInvoice_newInvoice_saved() {

        CreateInvoiceRequest request = CreateInvoiceRequest.builder()
                .bookingId("B1")
                .customerId("C1")
                .items(List.of(
                        InvoiceLineItemDto.builder()
                                .description("Service")
                                .unitPrice(100)
                                .quantity(2)
                                .build()
                ))
                .build();

        when(invoiceRepository.findByBookingId("B1"))
                .thenReturn(Optional.empty());

        billingService.createInvoice(request);

        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void createInvoice_duplicateInvoice_noSave() {

        when(invoiceRepository.findByBookingId("B1"))
                .thenReturn(Optional.of(new Invoice()));

        CreateInvoiceRequest request = CreateInvoiceRequest.builder()
                .bookingId("B1")
                .build();

        billingService.createInvoice(request);

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void markPaid_success() {

        Invoice invoice = Invoice.builder()
                .bookingId("B1")
                .invoiceStatus(InvoiceStatus.GENERATED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(invoiceRepository.findByBookingId("B1"))
                .thenReturn(Optional.of(invoice));

        when(invoiceRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        Invoice result = billingService.markPaid("B1");

        assertEquals(InvoiceStatus.PAID, result.getInvoiceStatus());
        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        assertNotNull(result.getPaidAt());
    }

    @Test
    void markPaid_invoiceNotFound_throwsException() {

        when(invoiceRepository.findByBookingId("B1"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> billingService.markPaid("B1"));
    }
}
