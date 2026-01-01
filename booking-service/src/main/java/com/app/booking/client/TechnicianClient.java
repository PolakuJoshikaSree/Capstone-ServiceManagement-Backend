package com.app.booking.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class TechnicianClient {

    private final WebClient webClient;

    // When booking is ASSIGNED
    public void markBusy(String technicianId, String bookingId) {
        webClient.put()
                .uri("/api/technicians/{id}/assign/{bookingId}", technicianId, bookingId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    // When booking is COMPLETED
    public void markAvailable(String technicianId) {
        webClient.put()
                .uri("/api/technicians/{id}/status/AVAILABLE", technicianId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
