package com.app.booking.model;

import java.time.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "customer-bookings")
public class Booking {
	
	@Id
    private String id;

    private String bookingId;
    private String customerId;
    
    private String technicianId;

    // From dropdowns
    private String serviceName;
    private String categoryName;

    private LocalDate scheduledDate;

    // Store exactly what UI sends
    private String timeSlot;   // "10:00 - 12:00"

    // Simple for MVP
    private String address;

    private String issueDescription;

    private String paymentMode; 

    private BookingStatus status;

    private LocalDateTime createdAt;
    
    private String serviceId;    
    private double servicePrice; 
}
