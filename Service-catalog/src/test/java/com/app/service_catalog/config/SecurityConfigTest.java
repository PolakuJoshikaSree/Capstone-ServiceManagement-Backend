package com.app.service_catalog.config;

import com.app.service_catalog.controller.ServiceController;
import com.app.service_catalog.security.JwtAuthFilter;
import com.app.service_catalog.service.ServiceItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ServiceController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // ===== REQUIRED BEANS =====

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private ServiceItemService serviceItemService;

    // ===== PUBLIC ENDPOINTS =====

    @Test
    void publicEndpoint_allowedWithoutHeaders() throws Exception {
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk());
    }

    @Test
    void publicEndpoint_allowedWithUserHeader() throws Exception {
        mockMvc.perform(get("/api/services")
                        .header("X-USER-ID", "u1"))
                .andExpect(status().isOk());
    }

    // ===== ADMIN ENDPOINTS =====

    @Test
    void adminEndpoint_withoutHeaders_isUnauthorized() throws Exception {
        mockMvc.perform(put("/api/services/svc1"))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoint_withUserRole_isForbidden() throws Exception {
        mockMvc.perform(put("/api/services/svc1")
                        .header("X-USER-ID", "u1")
                        .header("X-USER-ROLES", "ROLE_USER"))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoint_withAdminRole_isAllowed() throws Exception {
        mockMvc.perform(put("/api/services/svc1")
                        .header("X-USER-ID", "u1")
                        .header("X-USER-ROLES", "ROLE_ADMIN"))
                .andExpect(status().isOk());
    }

    // ===== CSRF =====

    @Test
    void csrfDisabled_allowsPutRequests() throws Exception {
        mockMvc.perform(put("/api/services/svc1")
                        .header("X-USER-ID", "u1")
                        .header("X-USER-ROLES", "ROLE_ADMIN"))
                .andExpect(status().isOk());
    }
}
