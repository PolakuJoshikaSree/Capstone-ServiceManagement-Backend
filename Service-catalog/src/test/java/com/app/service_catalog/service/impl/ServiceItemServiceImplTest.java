package com.app.service_catalog.service.impl;

import com.app.service_catalog.dto.request.CreateServiceRequest;
import com.app.service_catalog.dto.request.UpdateServiceRequest;
import com.app.service_catalog.dto.response.ServiceItemResponse;
import com.app.service_catalog.model.ServiceCategory;
import com.app.service_catalog.model.ServiceItem;
import com.app.service_catalog.repository.ServiceCategoryRepository;
import com.app.service_catalog.repository.ServiceItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceItemServiceImplTest {

    @Mock
    private ServiceItemRepository serviceRepository;

    @Mock
    private ServiceCategoryRepository categoryRepository;

    @InjectMocks
    private ServiceItemServiceImpl service;

    private ServiceCategory category;
    private ServiceItem serviceItem;

    @BeforeEach
    void setup() {
        category = ServiceCategory.builder()
                .id("cat1")
                .name("Plumbing")
                .servicesCount(0)
                .build();

        serviceItem = ServiceItem.builder()
                .id("svc1")
                .categoryId("cat1")
                .categoryName("Plumbing")
                .name("Pipe Fix")
                .basePrice(100)
                .taxPercentage(10)
                .discountPercentage(5)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    /* ---------------- CREATE ---------------- */

    @Test
    void createService_success_allFields() {

        CreateServiceRequest req = new CreateServiceRequest();
        req.setCategoryId("cat1");
        req.setName("Pipe Fix");
        req.setDescription("Fix pipe");
        req.setBasePrice(100.0);
        req.setCurrency("USD");
        req.setEstimatedDurationMinutes(60);
        req.setRequiredSkills(List.of("PLUMBING"));
        req.setTaxPercentage(10.0);
        req.setDiscountPercentage(5.0);
        req.setDiscountValidUntil(LocalDateTime.now().plusDays(1));

        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(category));
        when(serviceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ServiceItemResponse response = service.createService(req);

        assertEquals("Pipe Fix", response.getName());
        assertEquals(100.0, response.getPricingDetails().getFinalPrice());
        verify(categoryRepository).save(any());
    }

    @Test
    void createService_categoryNotFound_throwsException() {
        CreateServiceRequest req = new CreateServiceRequest();
        req.setCategoryId("invalid");

        when(categoryRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.createService(req));
    }

    /* ---------------- GET ---------------- */

    @Test
    void getServiceById_success() {
        when(serviceRepository.findById("svc1")).thenReturn(Optional.of(serviceItem));

        ServiceItemResponse response = service.getServiceById("svc1");

        assertEquals("Pipe Fix", response.getName());
    }

    @Test
    void getServiceById_notFound() {
        when(serviceRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getServiceById("x"));
    }

    @Test
    void getAllServices() {
        when(serviceRepository.findAll()).thenReturn(List.of(serviceItem));
        assertEquals(1, service.getAllServices().size());
    }

    @Test
    void getActiveServices() {
        when(serviceRepository.findByActiveTrue()).thenReturn(List.of(serviceItem));
        assertEquals(1, service.getActiveServices().size());
    }

    @Test
    void getServices_allBranches() {
        when(serviceRepository.findByCategoryIdAndActive("cat1", true))
                .thenReturn(List.of(serviceItem));
        when(serviceRepository.findByCategoryId("cat1"))
                .thenReturn(List.of(serviceItem));
        when(serviceRepository.findByActive(true))
                .thenReturn(List.of(serviceItem));
        when(serviceRepository.findAll())
                .thenReturn(List.of(serviceItem));

        assertEquals(1, service.getServices("cat1", true).size());
        assertEquals(1, service.getServices("cat1", null).size());
        assertEquals(1, service.getServices(null, true).size());
        assertEquals(1, service.getServices(null, null).size());
    }

    /* ---------------- UPDATE ---------------- */

    @Test
    void updateService_allFields() {
        UpdateServiceRequest req = new UpdateServiceRequest();
        req.setName("Updated");
        req.setBasePrice(200.0);
        req.setDiscountPercentage(10.0);

        when(serviceRepository.findById("svc1")).thenReturn(Optional.of(serviceItem));
        when(serviceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ServiceItemResponse response = service.updateService("svc1", req);

        assertEquals("Updated", response.getName());
        assertEquals(200.0, response.getPricingDetails().getFinalPrice());
    }

    @Test
    void updateServiceStatus() {
        when(serviceRepository.findById("svc1")).thenReturn(Optional.of(serviceItem));
        when(serviceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ServiceItemResponse response = service.updateServiceStatus("svc1", false);

        assertFalse(response.isActive());
    }

    /* ---------------- DELETE ---------------- */

    @Test
    void deleteService_success() {
        when(serviceRepository.findById("svc1")).thenReturn(Optional.of(serviceItem));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(category));

        service.deleteService("svc1");

        verify(serviceRepository).delete(serviceItem);
        verify(categoryRepository).save(any());
    }

    /* ---------------- SEARCH ---------------- */

    @Test
    void search_byQuery() {
        when(serviceRepository.findByNameContainingIgnoreCase("pipe"))
                .thenReturn(List.of(serviceItem));

        assertEquals(1, service.search("pipe", null).size());
    }

    @Test
    void search_bySkill() {
        when(serviceRepository.findByRequiredSkillsContainingIgnoreCase("PLUMBING"))
                .thenReturn(List.of(serviceItem));

        assertEquals(1, service.search(null, "PLUMBING").size());
    }

    @Test
    void search_noParams() {
        assertTrue(service.search(null, null).isEmpty());
    }
}
