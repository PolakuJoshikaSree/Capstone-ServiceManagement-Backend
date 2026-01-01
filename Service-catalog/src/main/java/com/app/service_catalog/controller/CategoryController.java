package com.app.service_catalog.controller;

import com.app.service_catalog.dto.request.*;
import com.app.service_catalog.dto.response.CategoryResponse;
import com.app.service_catalog.model.ServiceCategory;
import com.app.service_catalog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ---------------- CREATE (ADMIN ONLY) ----------------
    @PostMapping
    public CategoryResponse create(
            @RequestHeader("X-USER-ROLES") String roles,
            @Valid @RequestBody CreateCategoryRequest request) {

        requireAdmin(roles);

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
    @PutMapping("/{id}")
    public CategoryResponse update(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String id,
            @RequestBody UpdateCategoryRequest request) {

        requireAdmin(roles);
        return categoryService.updateCategory(id, request);
    }

    // ---------------- TOGGLE STATUS (ADMIN ONLY) ----------------
    @PutMapping("/{id}/status")
    public CategoryResponse updateStatus(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String id,
            @RequestBody UpdateCategoryStatusRequest request) {

        requireAdmin(roles);
        return categoryService.updateCategoryStatus(id, request.isActive());
    }

    // ---------------- REORDER (ADMIN ONLY) ----------------
    @PutMapping("/reorder")
    public void reorder(
            @RequestHeader("X-USER-ROLES") String roles,
            @RequestBody List<ReorderCategoryRequest> requests) {

        requireAdmin(roles);
        categoryService.reorderCategories(requests);
    }

    // ---------------- DELETE (ADMIN ONLY) ----------------
    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String id) {

        requireAdmin(roles);
        categoryService.deleteCategory(id);
    }

    // ===== helpers =====
    private void requireAdmin(String roles) {
        if (!roles.contains("ROLE_ADMIN")) {
            throw new SecurityException("Forbidden");
        }
    }
}
