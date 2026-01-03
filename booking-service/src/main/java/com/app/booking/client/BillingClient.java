package com.app.booking.client;

import com.app.booking.dto.billing.CreateInvoiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class BillingClient {

    private final WebClient webClient;

    public void createInvoice(CreateInvoiceRequest request) {
        webClient.post()
                .uri("http://billing-service/api/billing/invoices")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
