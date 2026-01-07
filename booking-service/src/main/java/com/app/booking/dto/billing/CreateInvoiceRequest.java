package com.app.booking.dto.billing;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {

    private String bookingId;
    private String customerId;
    private double subtotal;
    private List<InvoiceLineItem> items;
}
