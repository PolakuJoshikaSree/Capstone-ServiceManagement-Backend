package com.app.service_catalog.service.impl;

import com.app.service_catalog.dto.request.ReorderCategoryRequest;
import com.app.service_catalog.dto.request.UpdateCategoryRequest;
import com.app.service_catalog.dto.response.CategoryResponse;
import com.app.service_catalog.exception.BadRequestException;
import com.app.service_catalog.exception.ResourceNotFoundException;
import com.app.service_catalog.model.ServiceCategory;
import com.app.service_catalog.repository.ServiceCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private ServiceCategoryRepository repository;

    @InjectMocks
    private CategoryServiceImpl service;

    private ServiceCategory category;

    @BeforeEach
    void setup() {
        category = ServiceCategory.builder()
                .id("cat1")
                .name("Plumbing")
                .description("desc")
                .iconUrl("icon")
                .active(true)
                .displayOrder(1)
                .servicesCount(0)
                .createdAt(Instant.now())
                .build();
    }

    // ---------- CREATE ----------

    @Test
    void createCategory_success() {
        when(repository.existsByNameIgnoreCase("Plumbing")).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        CategoryResponse response = service.createCategory(category);

        assertEquals("Plumbing", response.getName());
        assertTrue(response.isActive());
        assertEquals(0, response.getServicesCount());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void createCategory_duplicateName_throwsException() {
        when(repository.existsByNameIgnoreCase("Plumbing")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> service.createCategory(category));
    }

    // ---------- READ ----------

    @Test
    void getAllCategories_success() {
        when(repository.findAll()).thenReturn(List.of(category));

        List<CategoryResponse> responses = service.getAllCategories();

        assertEquals(1, responses.size());
    }

    @Test
    void getActiveCategories_success() {
        when(repository.findByActiveTrueOrderByDisplayOrderAsc())
                .thenReturn(List.of(category));

        List<CategoryResponse> responses = service.getActiveCategories();

        assertEquals(1, responses.size());
        assertTrue(responses.get(0).isActive());
    }

    @Test
    void getCategoryById_success() {
        when(repository.findById("cat1")).thenReturn(Optional.of(category));

        CategoryResponse response = service.getCategoryById("cat1");

        assertEquals("cat1", response.getId());
    }

    @Test
    void getCategoryById_notFound() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getCategoryById("x"));
    }

    // ---------- UPDATE ----------

    @Test
    void updateCategory_allFields_success() {
        UpdateCategoryRequest req = new UpdateCategoryRequest();
        req.setName("Updated");
        req.setDescription("new desc");
        req.setIconUrl("new icon");
        req.setDisplayOrder(5);

        when(repository.findById("cat1")).thenReturn(Optional.of(category));
        when(repository.existsByNameIgnoreCase("Updated")).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        CategoryResponse response = service.updateCategory("cat1", req);

        assertEquals("Updated", response.getName());
        assertEquals("new desc", response.getDescription());
        assertEquals("new icon", response.getIconUrl());
        assertEquals(5, response.getDisplayOrder());
    }

    @Test
    void updateCategory_duplicateName_throwsException() {
        UpdateCategoryRequest req = new UpdateCategoryRequest();
        req.setName("Electrical");

        when(repository.findById("cat1")).thenReturn(Optional.of(category));
        when(repository.existsByNameIgnoreCase("Electrical")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> service.updateCategory("cat1", req));
    }

    @Test
    void updateCategory_notFound() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateCategory("x", new UpdateCategoryRequest()));
    }

    // ---------- STATUS ----------

    @Test
    void updateCategoryStatus_success() {
        when(repository.findById("cat1")).thenReturn(Optional.of(category));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        CategoryResponse response = service.updateCategoryStatus("cat1", false);

        assertFalse(response.isActive());
    }

    @Test
    void updateCategoryStatus_notFound() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateCategoryStatus("x", true));
    }

    // ---------- REORDER ----------

    @Test
    void reorderCategories_success() {

        ReorderCategoryRequest r1 = new ReorderCategoryRequest();
        r1.setId("cat1");
        r1.setDisplayOrder(10);

        when(repository.findById("cat1")).thenReturn(Optional.of(category));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.reorderCategories(List.of(r1));

        assertEquals(10, category.getDisplayOrder());
    }

    @Test
    void reorderCategories_notFound() {

        ReorderCategoryRequest r1 = new ReorderCategoryRequest();
        r1.setId("x");
        r1.setDisplayOrder(1);

        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.reorderCategories(List.of(r1)));
    }


    // ---------- DELETE ----------

    @Test
    void deleteCategory_success() {
        category.setServicesCount(0);
        when(repository.findById("cat1")).thenReturn(Optional.of(category));

        service.deleteCategory("cat1");

        verify(repository).delete(category);
    }

    @Test
    void deleteCategory_withServices_throwsException() {
        category.setServicesCount(2);
        when(repository.findById("cat1")).thenReturn(Optional.of(category));

        assertThrows(BadRequestException.class,
                () -> service.deleteCategory("cat1"));
    }

    @Test
    void deleteCategory_notFound() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.deleteCategory("x"));
    }
}
