package com.app.booking.service;

import com.app.booking.model.Technician;
import com.app.booking.repository.TechnicianRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnicianServiceTest {

    @Mock
    private TechnicianRepository technicianRepository;

    @InjectMocks
    private TechnicianService technicianService;

    @Test
    void markBusy_createsTechnicianIfNotExists() {

        when(technicianRepository.findByTechnicianId("TECH1"))
                .thenReturn(Optional.empty());

        technicianService.markBusy("TECH1");

        verify(technicianRepository).save(any(Technician.class));
    }

    @Test
    void markAvailable_success() {

        Technician technician = Technician.builder()
                .technicianId("TECH1")
                .build();

        when(technicianRepository.findByTechnicianId("TECH1"))
                .thenReturn(Optional.of(technician));

        technicianService.markAvailable("TECH1");

        verify(technicianRepository).save(technician);
    }
}
