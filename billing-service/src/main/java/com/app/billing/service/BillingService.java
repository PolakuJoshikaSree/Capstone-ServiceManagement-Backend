package com.app.billing.service;

import com.app.billing.dto.CreateInvoiceRequest;
import com.app.billing.dto.MonthlyRevenueDTO;
import com.app.billing.dto.notification.CreateNotificationRequest;
import com.app.billing.messaging.NotificationPublisher;
import com.app.billing.model.*;
import com.app.billing.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final NotificationPublisher notificationPublisher;

    public void createInvoice(CreateInvoiceRequest request) {

        // prevent duplicates
        if (invoiceRepository.findByBookingId(request.getBookingId()).isPresent()) {
            return;
        }

        // CALCULATE SUBTOTAL FROM ITEMS
        BigDecimal subtotal = request.getItems().stream()
                .map(i -> BigDecimal.valueOf(i.getUnitPrice())
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invoice subtotal cannot be zero");
        }

        BigDecimal tax = BigDecimal.ZERO;   // no tax
        BigDecimal total = subtotal;

        List<InvoiceLineItem> invoiceItems = request.getItems()
                .stream()
                .map(i -> new InvoiceLineItem(
                        i.getDescription(),
                        i.getUnitPrice(),
                        i.getQuantity()
                ))
                .toList();

        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8))
                .bookingId(request.getBookingId())
                .customerId(request.getCustomerId())
                .items(invoiceItems)
                .subtotal(subtotal.doubleValue())
                .tax(0.0)
                .totalAmount(total.doubleValue())
                .invoiceStatus(InvoiceStatus.GENERATED)
                .paymentStatus(PaymentStatus.PENDING)
                .issuedAt(LocalDateTime.now())
                .build();

        invoiceRepository.save(invoice);

        // notification
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
            System.err.println("Notification failed: " + e.getMessage());
        }
    }


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
