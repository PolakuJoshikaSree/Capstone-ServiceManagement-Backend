package com.app.booking.client;

import com.app.booking.dto.billing.CreateInvoiceRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class BillingClient {

    private final WebClient webClient;
    
    @CircuitBreaker(
            name = "billingService",
            fallbackMethod = "createInvoiceFallback"
    )
    public void createInvoice(CreateInvoiceRequest request) {

        webClient.post()
                .uri("http://billing-service/api/billing/invoices")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void createInvoiceFallback(
            CreateInvoiceRequest request,
            Throwable ex
    ) {
        System.out.println("CIRCUIT BREAKER TRIGGERED: Billing service is unavailable");
        System.out.println("Invoice will be generated later");
    }
}
