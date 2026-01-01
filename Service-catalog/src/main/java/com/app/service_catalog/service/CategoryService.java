package com.app.service_catalog.service;

import com.app.service_catalog.dto.request.ReorderCategoryRequest;
import com.app.service_catalog.dto.request.UpdateCategoryRequest;
import com.app.service_catalog.dto.response.CategoryResponse;
import com.app.service_catalog.model.ServiceCategory;

import java.util.List;

public interface CategoryService {

    // CREATE
    CategoryResponse createCategory(ServiceCategory category);

    // READ
    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getActiveCategories();

    CategoryResponse getCategoryById(String id);

    // UPDATE
    CategoryResponse updateCategory(String id, UpdateCategoryRequest request);

    CategoryResponse updateCategoryStatus(String id, boolean active);

    // REORDER
    void reorderCategories(List<ReorderCategoryRequest> requests);

    // DELETE
    void deleteCategory(String id);
}
