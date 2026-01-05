package com.app.billing.service;

import com.app.billing.dto.CreateInvoiceRequest;
import com.app.billing.dto.MonthlyRevenueDTO;
import com.app.billing.dto.notification.CreateNotificationRequest;
import com.app.billing.messaging.NotificationPublisher;
import com.app.billing.model.*;
import com.app.billing.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final NotificationPublisher notificationPublisher;

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

        // Send notification (NON-BLOCKING)
        try {
            notificationPublisher.send(
                new CreateNotificationRequest(
                    request.getCustomerId(),
                    "CUSTOMER",
                    "Invoice Generated",
                    "Your invoice has been generated successfully",
                    "INVOICE_CREATED"
                )
            );
        } catch (Exception e) {
            // log only — billing must not fail
            System.err.println("Notification failed: " + e.getMessage());
        }
    }

    // ================= MARK PAYMENT PAID =================
    public Invoice markPaid(String bookingId) {

        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        invoice.setInvoiceStatus(InvoiceStatus.PAID);
        invoice.setPaymentStatus(PaymentStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    // ================= ADMIN =================
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // ================= CUSTOMER =================
    public List<Invoice> getInvoicesByCustomer(String customerId) {
        return invoiceRepository.findByCustomerId(customerId);
    }

    // ================= REPORT =================
    public Map<String, Double> getMonthlyRevenue() {

        Map<String, Double> revenueMap = new LinkedHashMap<>();

        List<MonthlyRevenueDTO> data = invoiceRepository.getMonthlyRevenue();

        if (data == null || data.isEmpty()) {
            return revenueMap;
        }

        data.forEach(r -> {
            String key = r.getYear() + "-" + String.format("%02d", r.getMonth());
            revenueMap.put(key, r.getTotalRevenue());
        });

        return revenueMap;
    }
}
