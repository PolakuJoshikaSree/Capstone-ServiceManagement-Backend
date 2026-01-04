package com.app.billing.listener;

import com.app.billing.dto.CreateInvoiceRequest;
import com.app.billing.dto.InvoiceLineItemDto;
import com.app.billing.service.BillingService;
import com.app.billing.event.BookingCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingCompletedListener {

    private final BillingService billingService;

    @RabbitListener(queues = "billing.queue")
    public void onBookingCompleted(BookingCompletedEvent event) {

        log.info("Received booking completed event for {}", event.getBookingId());

        billingService.createInvoice(
                CreateInvoiceRequest.builder()
                        .bookingId(event.getBookingId())
                        .customerId(event.getCustomerId())
                        .items(List.of(
                                InvoiceLineItemDto.builder()
                                        .description(event.getServiceName())
                                        .unitPrice(499.0)
                                        .quantity(1)
                                        .build()
                        ))
                        .build()
        );

        log.info("Invoice created for booking {}", event.getBookingId());
    }
}
