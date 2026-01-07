package com.app.service_catalog.controller;

import com.app.service_catalog.dto.request.CreateServiceRequest;
import com.app.service_catalog.dto.request.UpdateServiceRequest;
import com.app.service_catalog.dto.response.ServiceItemResponse;
import com.app.service_catalog.model.ServiceItem;
import com.app.service_catalog.repository.ServiceItemRepository;
import com.app.service_catalog.service.ServiceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceItemService serviceItemService;
    private final ServiceItemRepository serviceItemRepository;

    // ================= CRUD =================

    @PostMapping
    public ServiceItemResponse create(
            @RequestHeader(value = "X-USER-ROLES", required = false) String roles,
            @RequestBody CreateServiceRequest request) {

        requireAdmin(roles);
        return serviceItemService.createService(request);
    }

    @GetMapping
    public List<ServiceItemResponse> getAll() {
        return serviceItemService.getAllServices();
    }

    @GetMapping("/active")
    public List<ServiceItemResponse> getActiveServices() {
        return serviceItemService.getActiveServices();
    }

    @GetMapping("/{serviceId}")
    public ServiceItemResponse getById(@PathVariable String serviceId) {
        return serviceItemService.getServiceById(serviceId);
    }

    @PutMapping("/{serviceId}")
    public ServiceItemResponse update(
            @RequestHeader(value = "X-USER-ROLES", required = false) String roles,
            @PathVariable String serviceId,
            @RequestBody UpdateServiceRequest request) {

        requireAdmin(roles);
        return serviceItemService.updateService(serviceId, request);
    }

    @PutMapping("/{serviceId}/status")
    public ServiceItemResponse updateStatus(
            @RequestHeader(value = "X-USER-ROLES", required = false) String roles,
            @PathVariable String serviceId,
            @RequestParam boolean active) {

        requireAdmin(roles);
        return serviceItemService.updateServiceStatus(serviceId, active);
    }

    @DeleteMapping("/{serviceId}")
    public void delete(
            @RequestHeader(value = "X-USER-ROLES", required = false) String roles,
            @PathVariable String serviceId) {

        requireAdmin(roles);
        serviceItemService.deleteService(serviceId);
    }

    @GetMapping("/search")
    public List<ServiceItemResponse> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String skill) {

        return serviceItemService.search(query, skill);
    }
    @GetMapping("/price")
    public Double getServicePrice(
            @RequestParam String serviceName,
            @RequestParam String categoryName
    ) {
        ServiceItem service = serviceItemRepository.findAll()
                .stream()
                .filter(s ->
                        s.isActive()
                        && s.getName().equalsIgnoreCase(serviceName)
                        && s.getCategoryName().equalsIgnoreCase(categoryName)
                )
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Service not found"));

        return Math.round(service.getBasePrice() * 100.0) / 100.0;
    }


    // ================= SECURITY =================
    private void requireAdmin(String roles) {
        if (roles == null || !roles.contains("ROLE_ADMIN")) {
            throw new SecurityException("Admin access required");
        }
    }
}
