package com.app.booking.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.app.booking.model.Technician;

public interface TechnicianRepository
        extends MongoRepository<Technician, String> {

    Optional<Technician> findByTechnicianId(String technicianId);
}
