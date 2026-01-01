package com.app.service_catalog.controller;

import com.app.service_catalog.dto.request.CreateServiceRequest;
import com.app.service_catalog.dto.request.UpdateServiceRequest;
import com.app.service_catalog.dto.response.ServiceItemResponse;
import com.app.service_catalog.service.ServiceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceItemService serviceItemService;

    // ---------------- CREATE (ADMIN ONLY) ----------------
    @PostMapping
    public ServiceItemResponse create(
            @RequestHeader("X-USER-ROLES") String roles,
            @RequestBody CreateServiceRequest request) {

        requireAdmin(roles);
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
    @PutMapping("/{serviceId}")
    public ServiceItemResponse update(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String serviceId,
            @RequestBody UpdateServiceRequest request) {

        requireAdmin(roles);
        return serviceItemService.updateService(serviceId, request);
    }

    // ---------------- TOGGLE STATUS (ADMIN ONLY) ----------------
    @PutMapping("/{serviceId}/status")
    public ServiceItemResponse updateStatus(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String serviceId,
            @RequestParam boolean active) {

        requireAdmin(roles);
        return serviceItemService.updateServiceStatus(serviceId, active);
    }

    // ---------------- DELETE (ADMIN ONLY) ----------------
    @DeleteMapping("/{serviceId}")
    public void delete(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String serviceId) {

        requireAdmin(roles);
        serviceItemService.deleteService(serviceId);
    }

    // ---------------- SEARCH (PUBLIC) ----------------
    @GetMapping("/search")
    public List<ServiceItemResponse> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String skill) {

        return serviceItemService.search(query, skill);
    }

    // ===== helpers =====
    private void requireAdmin(String roles) {
        if (!roles.contains("ROLE_ADMIN")) {
            throw new SecurityException("Forbidden");
        }
    }
}
