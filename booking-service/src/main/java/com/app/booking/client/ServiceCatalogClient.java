package com.app.booking.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ServiceCatalogClient {

    private final WebClient webClient;

    public double getServicePrice(String serviceName, String categoryName) {

        Double price = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")      
                        .port(8082)          
                        .path("/api/services/price")
                        .queryParam("serviceName", serviceName)
                        .queryParam("categoryName", categoryName)
                        .build())
                .retrieve()
                .bodyToMono(Double.class)
                .block();

        if (price == null) {
            throw new RuntimeException("Service price not found");
        }

        return price;
    }
}
