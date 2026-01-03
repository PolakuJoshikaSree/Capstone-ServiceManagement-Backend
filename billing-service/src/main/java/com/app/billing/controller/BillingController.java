package com.app.billing.controller;

import com.app.billing.dto.CreateInvoiceRequest;
import com.app.billing.model.Invoice;
import com.app.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing/invoices")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping
    public ResponseEntity<Void> createInvoice(
            @RequestBody CreateInvoiceRequest request) {

        billingService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{bookingId}/pay")
    public Invoice markPaid(@PathVariable String bookingId) {
        return billingService.markPaid(bookingId);
    }
}
