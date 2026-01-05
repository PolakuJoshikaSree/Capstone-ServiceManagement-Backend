package com.app.billing.listener;

import com.app.billing.event.BookingCompletedEvent;
import com.app.billing.service.BillingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BookingCompletedListenerTest {

    BillingService billingService = Mockito.mock(BillingService.class);
    BookingCompletedListener listener =
            new BookingCompletedListener(billingService);

    @Test
    void onBookingCompleted_createsInvoice() {

        BookingCompletedEvent event =
                new BookingCompletedEvent("BK1", "C1", "AC");

        listener.onBookingCompleted(event);

        Mockito.verify(billingService)
                .createInvoice(Mockito.any());
    }
}
