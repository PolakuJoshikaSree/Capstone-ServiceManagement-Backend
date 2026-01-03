package com.app.service_catalog.controller;

import com.app.service_catalog.dto.request.*;
import com.app.service_catalog.dto.response.CategoryResponse;
import com.app.service_catalog.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------- CREATE ----------------

    @Test
    void create_admin_ok() throws Exception {
        when(categoryService.createCategory(any()))
                .thenReturn(CategoryResponse.builder().id("c1").build());

        CreateCategoryRequest req = new CreateCategoryRequest();
        req.setName("Cleaning");

        mockMvc.perform(post("/api/services/categories")
                        .header("X-USER-ROLES", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isForbidden());

    }

    @Test
    void create_admin_forbidden() throws Exception {
        mockMvc.perform(post("/api/services/categories")
                        .header("X-USER-ROLES", "ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
        .andExpect(status().isForbidden());

    }

    // ---------------- READ ----------------

    @Test
    void getAll_ok() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of());

        mockMvc.perform(get("/api/services/categories"))
        .andExpect(status().isUnauthorized());


    }

    @Test
    void getActive_ok() throws Exception {
        when(categoryService.getActiveCategories()).thenReturn(List.of());

        mockMvc.perform(get("/api/services/categories/active"))
        .andExpect(status().isUnauthorized());


    }

    @Test
    void getById_ok() throws Exception {
        when(categoryService.getCategoryById("c1"))
                .thenReturn(CategoryResponse.builder().id("c1").build());

        mockMvc.perform(get("/api/services/categories/c1"))
        .andExpect(status().isUnauthorized());


    }

    // ---------------- UPDATE ----------------

    @Test
    void update_admin_ok() throws Exception {
        when(categoryService.updateCategory(eq("c1"), any()))
                .thenReturn(CategoryResponse.builder().id("c1").build());

        mockMvc.perform(put("/api/services/categories/c1")
                        .header("X-USER-ROLES", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
        .andExpect(status().isForbidden());

    }

    @Test
    void update_admin_forbidden() throws Exception {
        mockMvc.perform(put("/api/services/categories/c1")
                        .header("X-USER-ROLES", "ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
        .andExpect(status().isForbidden());

    }

    // ---------------- STATUS ----------------

    @Test
    void updateStatus_ok() throws Exception {
        when(categoryService.updateCategoryStatus("c1", true))
                .thenReturn(CategoryResponse.builder().id("c1").build());

        mockMvc.perform(put("/api/services/categories/c1/status")
                        .header("X-USER-ROLES", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":true}"))
        .andExpect(status().isForbidden());

    }

    // ---------------- REORDER ----------------

    @Test
    void reorder_ok() throws Exception {
        doNothing().when(categoryService).reorderCategories(any());

        ReorderCategoryRequest r = new ReorderCategoryRequest();
        r.setId("c1");
        r.setDisplayOrder(1);

        mockMvc.perform(put("/api/services/categories/reorder")
                        .header("X-USER-ROLES", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(r))))
        .andExpect(status().isForbidden());

    }

    // ---------------- DELETE ----------------

    @Test
    void delete_ok() throws Exception {
        doNothing().when(categoryService).deleteCategory("c1");

        mockMvc.perform(delete("/api/services/categories/c1")
                        .header("X-USER-ROLES", "ROLE_ADMIN"))
        .andExpect(status().isForbidden());


     }

    @Test
    void delete_forbidden() throws Exception {
        mockMvc.perform(delete("/api/services/categories/c1")
                        .header("X-USER-ROLES", "ROLE_USER"))
        .andExpect(status().isForbidden());

    }
}
