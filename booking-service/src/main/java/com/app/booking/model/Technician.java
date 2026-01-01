package com.app.booking.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "technicians")
public class Technician {

    @Id
    private String id;

    // This ID comes from auth-service userId
    private String technicianId;

    private String name;

    private TechnicianStatus status;
}
