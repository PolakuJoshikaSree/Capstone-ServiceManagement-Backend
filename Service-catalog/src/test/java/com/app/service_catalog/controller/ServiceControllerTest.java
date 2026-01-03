package com.app.service_catalog.controller;

import com.app.service_catalog.dto.request.UpdateServiceRequest;
import com.app.service_catalog.service.ServiceItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceItemService serviceItemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_ok() throws Exception {
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk());
    }

    @Test
    void getActive_ok() throws Exception {
        mockMvc.perform(get("/api/services/active"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_ok() throws Exception {
        mockMvc.perform(get("/api/services/svc1"))
                .andExpect(status().isOk());
    }

    @Test
    void search_ok() throws Exception {
        mockMvc.perform(get("/api/services/search"))
                .andExpect(status().isOk());
    }

    @Test
    void update_admin_ok() throws Exception {
        UpdateServiceRequest req = new UpdateServiceRequest();

        mockMvc.perform(put("/api/services/svc1")
                        .header("X-USER-ROLES", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatus_ok() throws Exception {
        mockMvc.perform(put("/api/services/svc1/status")
                        .header("X-USER-ROLES", "ROLE_ADMIN")
                        .param("active", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_ok() throws Exception {
        mockMvc.perform(delete("/api/services/svc1")
                        .header("X-USER-ROLES", "ROLE_ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void update_admin_forbidden() throws Exception {
        mockMvc.perform(put("/api/services/svc1")
                        .header("X-USER-ROLES", "ROLE_USER"))
        .andExpect(status().isNotFound());

    }

}
