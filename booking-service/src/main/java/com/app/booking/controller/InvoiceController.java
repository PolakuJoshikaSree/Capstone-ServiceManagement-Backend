package com.app.booking.controller;

import com.app.booking.model.Invoice;
import com.app.booking.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // -------- CUSTOMER INVOICES --------
    @GetMapping("/my")
    public List<Invoice> myInvoices(
            @RequestHeader("X-USER-ID") String customerId) {

        return invoiceService.getCustomerInvoices(customerId);
    }

    // -------- MARK PAID (ADMIN) --------
    @PutMapping("/{bookingId}/pay")
    public Invoice markPaid(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String bookingId) {

        if (!roles.contains("ROLE_ADMIN")) {
            throw new SecurityException("Forbidden");
        }

        return invoiceService.markInvoicePaid(bookingId);
    }
}
