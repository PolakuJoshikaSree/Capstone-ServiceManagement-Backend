package com.app.booking.dto.billing;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLineItem {

    private String description;
    private double unitPrice;
    private int quantity;
}
