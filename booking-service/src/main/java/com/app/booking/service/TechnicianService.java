package com.app.booking.service;

import com.app.booking.model.Technician;
import com.app.booking.model.TechnicianStatus;
import com.app.booking.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TechnicianService {

    private final TechnicianRepository technicianRepository;

    public void markBusy(String technicianId) {

        Technician technician = technicianRepository
                .findByTechnicianId(technicianId)
                .orElse(
                        Technician.builder()
                                .technicianId(technicianId)
                                .status(TechnicianStatus.AVAILABLE)
                                .build()
                );

        technician.setStatus(TechnicianStatus.BUSY);
        technicianRepository.save(technician);
    }

    public void markAvailable(String technicianId) {

        Technician technician = technicianRepository
                .findByTechnicianId(technicianId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Technician not found"));

        technician.setStatus(TechnicianStatus.AVAILABLE);
        technicianRepository.save(technician);
    }
}
