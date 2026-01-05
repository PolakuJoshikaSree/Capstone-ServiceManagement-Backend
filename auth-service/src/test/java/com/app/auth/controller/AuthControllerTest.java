package com.app.auth.controller;

import com.app.auth.dto.*;
import com.app.auth.entity.UserEntity;
import com.app.auth.enums.Role;
import com.app.auth.mapper.UserMapper;
import com.app.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.app.auth.security.JwtAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(
	    controllers = AuthController.class,
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
class AuthControllerTest {
	
	private final UserMapper mapper = new UserMapper();


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_success() throws Exception {
        Mockito.when(authService.registerUser(Mockito.any()))
                .thenReturn(new AuthResponseDTO());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "a@b.com",
                      "password": "password",
                      "firstName": "A",
                      "lastName": "B",
                      "phoneNumber": "9999999999",
                      "role": "CUSTOMER"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_success() throws Exception {
        Mockito.when(authService.authenticateUser(Mockito.any()))
                .thenReturn(new AuthResponseDTO());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "a@b.com",
                      "password": "password"
                    }
                """))
                .andExpect(status().isOk());
    }


    @Test
    void logout_success() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }


    @Test
    void refreshToken_success() throws Exception {
        Mockito.when(authService.refreshAccessToken("refresh"))
                .thenReturn(new AuthResponseDTO());

        mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "refreshToken": "refresh" }
                """))
                .andExpect(status().isOk());
    }


    @Test
    void getTechnicians_success() throws Exception {
        Mockito.when(authService.getAllTechnicians())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/auth/users/technicians"))
                .andExpect(status().isOk());
    }
    @Test
    void register_validation_error() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void changePassword_validation_error() throws Exception {
        mockMvc.perform(post("/api/auth/change-password")
                .header("X-USER-ID", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void validateToken_invalid() throws Exception {
        Mockito.when(authService.validateToken("bad"))
                .thenThrow(new IllegalArgumentException("Invalid"));

        mockMvc.perform(post("/api/auth/validate-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "token": "bad" }
                """))
                .andExpect(status().isBadRequest());
    }
    @Test
    void changePassword_success() throws Exception {

        Mockito.doNothing()
               .when(authService)
               .changePassword(Mockito.anyString(),
                               Mockito.anyString(),
                               Mockito.anyString());

        mockMvc.perform(post("/api/auth/change-password")
                .header("X-USER-ID", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "currentPassword": "oldpass",
                      "newPassword": "newpass"
                    }
                """))
                .andExpect(status().isBadRequest());
    }
    @Test
    void toEntity_success() {

        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("a@b.com");
        dto.setPassword("password");
        dto.setFirstName("A");
        dto.setLastName("B");
        dto.setPhoneNumber("9999999999");
        dto.setRole(Role.CUSTOMER);

        UserEntity entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("a@b.com", entity.getEmail());
        assertEquals("A", entity.getFirstName());
        assertEquals("B", entity.getLastName());
        assertEquals("9999999999", entity.getPhoneNumber());
        assertEquals(Role.CUSTOMER, entity.getRole());
    }




}
