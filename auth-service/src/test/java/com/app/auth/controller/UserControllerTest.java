package com.app.auth.controller;

import com.app.auth.dto.UpdateAccountStatusDTO;
import com.app.auth.dto.UpdateRoleDTO;
import com.app.auth.dto.UserProfileResponseDTO;
import com.app.auth.enums.AccountStatus;
import com.app.auth.enums.Role;
import com.app.auth.security.JwtAuthenticationFilter;
import com.app.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;
    
    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getProfile_success() throws Exception {
        Mockito.when(userService.getUserProfile("1"))
                .thenReturn(new UserProfileResponseDTO());

        mockMvc.perform(get("/api/users/profile")
                        .header("X-USER-ID", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_adminAllowed() throws Exception {
        Mockito.when(userService.getUserProfile("2"))
                .thenReturn(new UserProfileResponseDTO());

        mockMvc.perform(get("/api/users/2")
                        .header("X-USER-ROLES", "ROLE_ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_forbidden() throws Exception {
        mockMvc.perform(get("/api/users/2")
                        .header("X-USER-ROLES", "ROLE_CUSTOMER"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateStatus_adminAllowed() throws Exception {
        Mockito.when(userService.updateAccountStatus(eq("2"), any()))
                .thenReturn(new UserProfileResponseDTO());

        UpdateAccountStatusDTO dto = new UpdateAccountStatusDTO();
        dto.setAccountStatus(AccountStatus.ACTIVE);

        mockMvc.perform(put("/api/users/2/status")
                        .header("X-USER-ROLES", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateRole_adminAllowed() throws Exception {
        Mockito.when(userService.updateUserRole(eq("2"), any()))
                .thenReturn(new UserProfileResponseDTO());

        UpdateRoleDTO dto = new UpdateRoleDTO();
        dto.setRole(Role.MANAGER);

        mockMvc.perform(put("/api/users/2/role")
                        .header("X-USER-ROLES", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_adminAllowed() throws Exception {
        Mockito.doNothing().when(userService).deleteUser("2");

        mockMvc.perform(delete("/api/users/2")
                        .header("X-USER-ROLES", "ROLE_ADMIN"))
                .andExpect(status().isOk());
    }
}
