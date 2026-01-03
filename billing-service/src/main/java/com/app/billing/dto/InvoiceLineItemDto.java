package com.app.billing.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceLineItemDto {

    private String description;
    private double unitPrice;
    private int quantity;
}
