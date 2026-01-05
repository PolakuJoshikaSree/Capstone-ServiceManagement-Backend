package com.app.billing.controller;

import com.app.billing.dto.CreateInvoiceRequest;
import com.app.billing.model.Invoice;
import com.app.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing/invoices")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    // ================= CREATE INVOICE (FROM BOOKING SERVICE) =================
    @PostMapping
    public ResponseEntity<Void> createInvoice(
            @RequestBody CreateInvoiceRequest request) {

        billingService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ================= DUMMY PAYMENT =================
    @PutMapping("/{bookingId}/pay")
    public ResponseEntity<Invoice> markPaid(@PathVariable String bookingId) {
        return ResponseEntity.ok(billingService.markPaid(bookingId));
    }

    // ================= ADMIN =================
    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(billingService.getAllInvoices());
    }

    // ================= CUSTOMER (THIS FIXES YOUR UI) =================
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Invoice>> getInvoicesByCustomer(
            @PathVariable String customerId) {

        return ResponseEntity.ok(
                billingService.getInvoicesByCustomer(customerId)
        );
    }
    @GetMapping("/reports/monthly-revenue")
    public ResponseEntity<Map<String, Double>> getMonthlyRevenue() {
        return ResponseEntity.ok(billingService.getMonthlyRevenue());
    }


}
