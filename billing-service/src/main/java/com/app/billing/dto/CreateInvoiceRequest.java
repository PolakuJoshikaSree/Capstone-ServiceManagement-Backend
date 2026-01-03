package com.app.billing.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInvoiceRequest {

    private String bookingId;
    private String customerId;
    private List<InvoiceLineItemDto> items;
}
