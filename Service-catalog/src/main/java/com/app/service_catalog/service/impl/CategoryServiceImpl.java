package com.app.service_catalog.service.impl;

import com.app.service_catalog.dto.request.ReorderCategoryRequest;
import com.app.service_catalog.dto.request.UpdateCategoryRequest;
import com.app.service_catalog.dto.response.CategoryResponse;
import com.app.service_catalog.exception.BadRequestException;
import com.app.service_catalog.exception.ResourceNotFoundException;
import com.app.service_catalog.model.ServiceCategory;
import com.app.service_catalog.repository.ServiceCategoryRepository;
import com.app.service_catalog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final ServiceCategoryRepository repository;

    // ---------------- CREATE ----------------
    @Override
    public CategoryResponse createCategory(ServiceCategory category) {

        if (repository.existsByNameIgnoreCase(category.getName())) {
            throw new BadRequestException("Category with this name already exists");
        }

        category.setActive(true);
        category.setServicesCount(0);
        category.setCreatedAt(Instant.now());

        ServiceCategory saved = repository.save(category);
        return mapToResponse(saved);
    }

    // ---------------- READ ----------------
    @Override
    public List<CategoryResponse> getAllCategories() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CategoryResponse> getActiveCategories() {
        return repository.findByActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(String id) {
        ServiceCategory category = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found")
                );

        return mapToResponse(category);
    }

    // ---------------- UPDATE ----------------
    @Override
    public CategoryResponse updateCategory(String id, UpdateCategoryRequest request) {

        ServiceCategory category = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found")
                );

        if (request.getName() != null &&
                !request.getName().equalsIgnoreCase(category.getName()) &&
                repository.existsByNameIgnoreCase(request.getName())) {

            throw new BadRequestException("Category with this name already exists");
        }

        if (request.getName() != null)
            category.setName(request.getName());
        if (request.getDescription() != null)
            category.setDescription(request.getDescription());
        if (request.getIconUrl() != null)
            category.setIconUrl(request.getIconUrl());
        if (request.getDisplayOrder() != null)
            category.setDisplayOrder(request.getDisplayOrder());

        return mapToResponse(repository.save(category));
    }

    // ---------------- STATUS ----------------
    @Override
    public CategoryResponse updateCategoryStatus(String id, boolean active) {

        ServiceCategory category = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found")
                );

        category.setActive(active);
        return mapToResponse(repository.save(category));
    }

    // ---------------- REORDER ----------------
    @Override
    public void reorderCategories(List<ReorderCategoryRequest> requests) {

        for (ReorderCategoryRequest req : requests) {
            ServiceCategory category = repository.findById(req.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Category not found")
                    );

            category.setDisplayOrder(req.getDisplayOrder());
            repository.save(category);
        }
    }

    // ---------------- DELETE ----------------
    @Override
    public void deleteCategory(String id) {

        ServiceCategory category = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found")
                );

        if (category.getServicesCount() > 0) {
            throw new BadRequestException("Cannot delete category with services");
        }

        repository.delete(category);
    }

    // ---------------- MAPPER ----------------
    private CategoryResponse mapToResponse(ServiceCategory category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .iconUrl(category.getIconUrl())
                .active(category.isActive())
                .displayOrder(category.getDisplayOrder())
                .servicesCount(category.getServicesCount())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
