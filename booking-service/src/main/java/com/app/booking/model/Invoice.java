package com.app.booking.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "invoices")
public class Invoice {

    @Id
    private String id;

    private String invoiceNumber;
    private String bookingId;
    private String customerId;

    private List<InvoiceLineItem> items;

    private double subtotal;
    private double tax;
    private double totalAmount;

    private InvoiceStatus invoiceStatus;
    private PaymentStatus paymentStatus;

    private LocalDateTime issuedAt;
    private LocalDateTime paidAt;
}
