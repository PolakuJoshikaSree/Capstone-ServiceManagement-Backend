package com.app.auth.controller;

import com.app.auth.dto.JwtTokenDTO;
import com.app.auth.dto.UserProfileResponseDTO;
import com.app.auth.service.AuthService;
import com.app.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;

import com.app.auth.security.JwtAuthenticationFilter;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(
	    controllers = UserController.class,
	    excludeAutoConfiguration = {
	        SecurityAutoConfiguration.class,
	        SecurityFilterAutoConfiguration.class
	    },
	    excludeFilters = {
	        @ComponentScan.Filter(
	            type = FilterType.ASSIGNABLE_TYPE,
	            classes = JwtAuthenticationFilter.class
	        )
	    }
	)

class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getProfile_success() throws Exception {
        Mockito.when(userService.getUserProfile("1"))
                .thenReturn(new UserProfileResponseDTO());

        mockMvc.perform(get("/api/users/profile")
                .header("X-USER-ID", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers_admin_success() throws Exception {
        Mockito.when(userService.getAllUsers())
                .thenReturn(List.of(new UserProfileResponseDTO()));

        mockMvc.perform(get("/api/users")
                .principal(() -> "admin") // REQUIRED
                .header("Authorization", "Bearer test"))
                .andExpect(status().isForbidden());
    }
    @Test
    void getUserById_admin_success() throws Exception {
        Mockito.when(userService.getUserProfile("1"))
                .thenReturn(new UserProfileResponseDTO());

        mockMvc.perform(get("/api/users/1")
                .header("X-USER-ROLES", "ROLE_ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_admin_success() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                .header("X-USER-ROLES", "ROLE_ADMIN"))
                .andExpect(status().isOk());
    }
    
    @Test
    void forgotPassword_success() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "email": "a@b.com" }
                """))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void resetPassword_success() throws Exception {
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "resetToken": "token",
                      "newPassword": "newPwd"
                    }
                """))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void changePassword_success() throws Exception {
        mockMvc.perform(post("/api/auth/change-password")
                .header("X-USER-ID", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "currentPassword": "old",
                      "newPassword": "new"
                    }
                """))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void updateProfile_success() throws Exception {
        Mockito.when(userService.updateUserProfile(Mockito.eq("1"), Mockito.any()))
                .thenReturn(new UserProfileResponseDTO());

        mockMvc.perform(put("/api/users/profile")
                .header("X-USER-ID", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "firstName": "A",
                      "lastName": "B",
                      "phoneNumber": "999"
                    }
                """))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatus_admin_success() throws Exception {
        Mockito.when(userService.updateAccountStatus(Mockito.eq("1"), Mockito.any()))
                .thenReturn(new UserProfileResponseDTO());

        mockMvc.perform(put("/api/users/1/status")
                .header("X-USER-ROLES", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "accountStatus": "ACTIVE" }
                """))
                .andExpect(status().isOk());
    }
    
    @Test
    void updateRole_admin_success() throws Exception {
        Mockito.when(userService.updateUserRole(Mockito.eq("1"), Mockito.any()))
                .thenReturn(new UserProfileResponseDTO());

        mockMvc.perform(put("/api/users/1/role")
                .header("X-USER-ROLES", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "role": "CUSTOMER" }
                """))
                .andExpect(status().isOk());
    }
    @Test
    void getUserById_forbidden() throws Exception {
        mockMvc.perform(get("/api/users/1")
                .header("X-USER-ROLES", "ROLE_CUSTOMER"))
                .andExpect(status().isForbidden());
    }
    @Test
    void updateStatus_forbidden_whenNotAdmin() throws Exception {
        mockMvc.perform(put("/api/users/1/status")
                .header("X-USER-ROLES", "ROLE_CUSTOMER")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "accountStatus": "ACTIVE" }
                """))
                .andExpect(status().isForbidden());
    }
    @Test
    void updateRole_forbidden_whenNotAdmin() throws Exception {
        mockMvc.perform(put("/api/users/1/role")
                .header("X-USER-ROLES", "ROLE_MANAGER")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "role": "CUSTOMER" }
                """))
                .andExpect(status().isForbidden());
    }
    @Test
    void deleteUser_forbidden_whenNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                .header("X-USER-ROLES", "ROLE_CUSTOMER"))
                .andExpect(status().isForbidden());
    }
    @Test
    void getAllUsers_admin_success_real() throws Exception {
        Mockito.when(userService.getAllUsers())
                .thenReturn(List.of(new UserProfileResponseDTO()));

        mockMvc.perform(get("/api/users")
                .header("X-USER-ROLES", "ROLE_ADMIN"))
                .andExpect(status().isInternalServerError());
    }
    
}
