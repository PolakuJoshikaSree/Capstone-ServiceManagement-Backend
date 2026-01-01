package com.app.booking.service;

import com.app.booking.model.*;
import com.app.booking.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public Invoice generateInvoice(
            String bookingId,
            String customerId,
            List<InvoiceLineItem> items
    ) {

        double subtotal = items.stream()
                .mapToDouble(InvoiceLineItem::getTotal)
                .sum();

        double tax = subtotal * 0.18; // 18% GST
        double total = subtotal + tax;

        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8))
                .bookingId(bookingId)
                .customerId(customerId)
                .items(items)
                .subtotal(subtotal)
                .tax(tax)
                .totalAmount(total)
                .invoiceStatus(InvoiceStatus.GENERATED)
                .paymentStatus(PaymentStatus.PENDING)
                .issuedAt(LocalDateTime.now())
                .build();

        return invoiceRepository.save(invoice);
    }

    public Invoice markInvoicePaid(String bookingId) {

        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invoice not found"));

        invoice.setInvoiceStatus(InvoiceStatus.PAID);
        invoice.setPaymentStatus(PaymentStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getCustomerInvoices(String customerId) {
        return invoiceRepository.findByCustomerId(customerId);
    }
    public Invoice generateInvoiceIfAbsent(
            String bookingId,
            String customerId,
            List<InvoiceLineItem> items) {

        // Check if invoice already exists for this booking
        return invoiceRepository.findByBookingId(bookingId)
                .orElseGet(() -> {

                    double subtotal = items.stream()
                            .mapToDouble(InvoiceLineItem::getTotal)
                            .sum();

                    double tax = subtotal * 0.18; // 18% GST
                    double total = subtotal + tax;

                    Invoice invoice = Invoice.builder()
                            .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8))
                            .bookingId(bookingId)
                            .customerId(customerId)
                            .items(items)
                            .subtotal(subtotal)
                            .tax(tax)
                            .totalAmount(total)
                            .invoiceStatus(InvoiceStatus.GENERATED)
                            .paymentStatus(PaymentStatus.PENDING)
                            .issuedAt(LocalDateTime.now())
                            .build();

                    return invoiceRepository.save(invoice);
                });
    }
}
