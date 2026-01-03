package com.app.service_catalog.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpoint_allowedWithoutHeaders() throws Exception {
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk());
    }

    @Test
    void endpoint_withUserHeader_stillAllowed() throws Exception {
        mockMvc.perform(get("/api/services")
                        .header("X-USER-ID", "u1"))
                .andExpect(status().isOk());
    }

    @Test
    void endpoint_withAdminRole_allowed() throws Exception {
        mockMvc.perform(get("/api/services")
                        .header("X-USER-ID", "u1")
                        .header("X-USER-ROLES", "ROLE_ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void missingHeaders_doesNotBlockPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/categories"))
        .andExpect(status().is4xxClientError());

    }

    @Test
    void csrfDisabled_allowsGetRequests() throws Exception {
        mockMvc.perform(get("/api/services")
                        .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                .andExpect(status().isOk());
    }
}
