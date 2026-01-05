package com.app.auth.service;

import com.app.auth.dto.*;
import com.app.auth.entity.UserEntity;
import com.app.auth.enums.Role;
import com.app.auth.mapper.UserMapper;
import com.app.auth.repository.UserRepository;
import com.app.auth.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserMapper userMapper;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenService jwtTokenService;
    @Mock JwtUtil jwtUtil;

    @InjectMocks
    AuthService authService;

    /* ================= REGISTER ================= */

    @Test
    void registerUser_success() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("a@b.com");
        dto.setPhoneNumber("999");
        dto.setPassword("pwd");
        dto.setRole(Role.CUSTOMER);

        UserEntity user = new UserEntity();
        user.setId("1");
        user.setRole(Role.CUSTOMER);

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(passwordEncoder.encode("pwd")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(user);

        when(jwtTokenService.generateAccessToken(user)).thenReturn("access");
        when(jwtTokenService.generateRefreshToken(user)).thenReturn("refresh");
        when(jwtUtil.getIssuedAt(any())).thenReturn(LocalDateTime.now());
        when(jwtUtil.getExpiresAt(any())).thenReturn(LocalDateTime.now().plusHours(1));

        assertNotNull(authService.registerUser(dto));
    }

    /* ================= REFRESH TOKEN ================= */

    @Test
    void refreshAccessToken_success() {
        JwtTokenDTO stored = new JwtTokenDTO();
        stored.setUserId("1");

        UserEntity user = new UserEntity();
        user.setId("1");

        when(jwtTokenService.getTokenFromRedis("refresh")).thenReturn(stored);
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(jwtTokenService.generateAccessToken(user)).thenReturn("newAccess");
        when(jwtUtil.getIssuedAt(any())).thenReturn(LocalDateTime.now());
        when(jwtUtil.getExpiresAt(any())).thenReturn(LocalDateTime.now().plusMinutes(30));

        assertNotNull(authService.refreshAccessToken("refresh"));
    }

    /* ================= VALIDATE TOKEN ================= */

    @Test
    void validateToken_success() {
        JwtTokenDTO dto = new JwtTokenDTO();
        when(jwtTokenService.getTokenFromRedis("token")).thenReturn(dto);

        assertNotNull(authService.validateToken("token"));
    }

    /* ================= CHANGE PASSWORD ================= */

    @Test
    void changePassword_success() {
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setPassword("old");

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPwd", "old")).thenReturn(true);
        when(passwordEncoder.encode("newPwd")).thenReturn("encoded");

        authService.changePassword("1", "oldPwd", "newPwd");

        verify(jwtTokenService).removeAllTokensForUser("1");
    }

    /* ================= FORGOT PASSWORD ================= */

    @Test
    void initiatePasswordReset_success() {
        UserEntity user = new UserEntity();
        user.setEmail("a@b.com");

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(jwtTokenService.generatePasswordResetToken("a@b.com")).thenReturn("token");

        authService.initiatePasswordReset("a@b.com");

        verify(jwtTokenService).storePasswordResetToken(eq("a@b.com"), any());
    }

    /* ================= RESET PASSWORD ================= */

    @Test
    void resetPassword_success() {
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setEmail("a@b.com");

        when(jwtTokenService.validatePasswordResetToken("token")).thenReturn("a@b.com");
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPwd")).thenReturn("encoded");

        authService.resetPassword("token", "newPwd");

        verify(jwtTokenService).removeAllTokensForUser("1");
    }

    /* ================= TECHNICIANS ================= */

    @Test
    void getAllTechnicians_success() {
        UserEntity tech = new UserEntity();
        tech.setId("1");
        tech.setFirstName("Tech");
        tech.setRole(Role.TECHNICIAN);

        when(userRepository.findByRole(Role.TECHNICIAN))
                .thenReturn(List.of(tech));

        assertEquals(1, authService.getAllTechnicians().size());
    }
}
