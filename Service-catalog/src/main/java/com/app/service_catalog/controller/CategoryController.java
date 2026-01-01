package com.app.service_catalog.controller;

import com.app.service_catalog.dto.request.CreateCategoryRequest;
import com.app.service_catalog.dto.request.ReorderCategoryRequest;
import com.app.service_catalog.dto.request.UpdateCategoryRequest;
import com.app.service_catalog.dto.request.UpdateCategoryStatusRequest;
import com.app.service_catalog.dto.response.CategoryResponse;
import com.app.service_catalog.model.ServiceCategory;
import com.app.service_catalog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ---------------- CREATE (ADMIN ONLY) ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CategoryResponse create(@Valid @RequestBody CreateCategoryRequest request) {

        ServiceCategory category = ServiceCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .displayOrder(request.getDisplayOrder())
                .build();

        return categoryService.createCategory(category);
    }

    // ---------------- READ ALL (PUBLIC) ----------------
    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryService.getAllCategories();
    }

    // ---------------- READ ACTIVE (PUBLIC) ----------------
    @GetMapping("/active")
    public List<CategoryResponse> getActive() {
        return categoryService.getActiveCategories();
    }

    // ---------------- READ BY ID (PUBLIC) ----------------
    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable String id) {
        return categoryService.getCategoryById(id);
    }

    // ---------------- UPDATE (ADMIN ONLY) ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CategoryResponse update(
            @PathVariable String id,
            @RequestBody UpdateCategoryRequest request
    ) {
        return categoryService.updateCategory(id, request);
    }

    // ---------------- TOGGLE STATUS (ADMIN ONLY) ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public CategoryResponse updateStatus(
            @PathVariable String id,
            @RequestBody UpdateCategoryStatusRequest request
    ) {
        return categoryService.updateCategoryStatus(id, request.isActive());
    }

    // ---------------- REORDER (ADMIN ONLY) ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/reorder")
    public void reorder(@RequestBody List<ReorderCategoryRequest> requests) {
        categoryService.reorderCategories(requests);
    }

    // ---------------- DELETE (ADMIN ONLY) ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        categoryService.deleteCategory(id);
    }
}
