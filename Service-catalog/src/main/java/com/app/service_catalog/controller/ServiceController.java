package com.app.service_catalog.controller;

import com.app.service_catalog.dto.request.CreateServiceRequest;
import com.app.service_catalog.dto.request.UpdateServiceRequest;
import com.app.service_catalog.dto.response.ServiceItemResponse;
import com.app.service_catalog.service.ServiceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceItemService serviceItemService;

    // ---------------- CREATE (ADMIN ONLY) ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ServiceItemResponse create(@RequestBody CreateServiceRequest request) {
        return serviceItemService.createService(request);
    }

    // ---------------- READ ALL (PUBLIC) ----------------
    @GetMapping
    public List<ServiceItemResponse> getAll() {
        return serviceItemService.getAllServices();
    }

    // ---------------- READ ACTIVE (PUBLIC) ----------------
    @GetMapping("/active")
    public List<ServiceItemResponse> getActiveServices() {
        return serviceItemService.getActiveServices();
    }

    // ---------------- READ BY ID (PUBLIC) ----------------
    @GetMapping("/{serviceId}")
    public ServiceItemResponse getById(@PathVariable String serviceId) {
        return serviceItemService.getServiceById(serviceId);
    }

    // ---------------- UPDATE (ADMIN ONLY) ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{serviceId}")
    public ServiceItemResponse update(
            @PathVariable String serviceId,
            @RequestBody UpdateServiceRequest request
    ) {
        return serviceItemService.updateService(serviceId, request);
    }

    // ---------------- TOGGLE STATUS (ADMIN ONLY) ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{serviceId}/status")
    public ServiceItemResponse updateStatus(
            @PathVariable String serviceId,
            @RequestParam boolean active
    ) {
        return serviceItemService.updateServiceStatus(serviceId, active);
    }

    // ---------------- DELETE (ADMIN ONLY) ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{serviceId}")
    public void delete(@PathVariable String serviceId) {
        serviceItemService.deleteService(serviceId);
    }

    // ---------------- SEARCH (PUBLIC) ----------------
    @GetMapping("/search")
    public List<ServiceItemResponse> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String skill
    ) {
        return serviceItemService.search(query, skill);
    }
}
