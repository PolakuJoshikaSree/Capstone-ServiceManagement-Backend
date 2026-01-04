package com.app.billing.service;

import com.app.billing.dto.CreateInvoiceRequest;
import com.app.billing.model.*;
import com.app.billing.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;

    // ================= CREATE INVOICE =================
    public void createInvoice(CreateInvoiceRequest request) {

        // Avoid duplicate invoice for same booking
        if (invoiceRepository.findByBookingId(request.getBookingId()).isPresent()) {
            return;
        }

        double subtotal = request.getItems().stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        double tax = subtotal * 0.18;
        double total = subtotal + tax;

        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8))
                .bookingId(request.getBookingId())
                .customerId(request.getCustomerId())
                .items(
                        request.getItems().stream()
                                .map(i -> new InvoiceLineItem(
                                        i.getDescription(),
                                        i.getUnitPrice(),
                                        i.getQuantity()
                                ))
                                .toList()
                )
                .subtotal(subtotal)
                .tax(tax)
                .totalAmount(total)
                .invoiceStatus(InvoiceStatus.GENERATED)
                .paymentStatus(PaymentStatus.PENDING)
                .issuedAt(LocalDateTime.now())
                .build();

        invoiceRepository.save(invoice);
    }

    // ================= MARK PAYMENT PAID (DUMMY PAYMENT) =================
    public Invoice markPaid(String bookingId) {

        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        invoice.setInvoiceStatus(InvoiceStatus.PAID);
        invoice.setPaymentStatus(PaymentStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    // ================= ADMIN / DEBUG =================
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // ================= CUSTOMER =================
    public List<Invoice> getInvoicesByCustomer(String customerId) {
        return invoiceRepository.findByCustomerId(customerId);
    }
}
