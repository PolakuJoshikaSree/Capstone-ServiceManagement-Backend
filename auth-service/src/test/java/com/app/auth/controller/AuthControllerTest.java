package com.app.auth.controller;

import com.app.auth.dto.*;
import com.app.auth.enums.Role;
import com.app.auth.security.JwtAuthenticationFilter;
import com.app.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    // 🔥 CRITICAL FIX
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_success() throws Exception {
        Mockito.when(authService.registerUser(any()))
                .thenReturn(new AuthResponseDTO());

        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("a@test.com");
        dto.setPassword("Password@1");
        dto.setFirstName("A");
        dto.setLastName("B");
        dto.setPhoneNumber("+919999999999");
        dto.setRole(Role.CUSTOMER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void login_success() throws Exception {
        Mockito.when(authService.authenticateUser(any()))
                .thenReturn(new AuthResponseDTO());

        AuthRequestDTO dto = new AuthRequestDTO();
        dto.setEmail("a@test.com");
        dto.setPassword("Password@1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void logout_withBearerToken_callsService() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());

        Mockito.verify(authService).logout("token");
    }

    @Test
    void refreshToken_success() throws Exception {
        Mockito.when(authService.refreshAccessToken(any()))
                .thenReturn(new AuthResponseDTO());

        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"r\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void validateToken_success() throws Exception {
        Mockito.when(authService.validateToken(any()))
                .thenReturn(new JwtTokenDTO());

        mockMvc.perform(post("/api/auth/validate-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"t\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getTechnicians_success() throws Exception {
        Mockito.when(authService.getAllTechnicians())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/auth/users/technicians"))
                .andExpect(status().isOk());
    }
}
