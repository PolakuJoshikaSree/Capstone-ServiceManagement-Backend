package com.app.booking.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLineItem {

    private String description;
    private double unitPrice;
    private int quantity;

    public double getTotal() {
        return unitPrice * quantity;
    }
}
