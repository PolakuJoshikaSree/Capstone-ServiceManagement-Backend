package com.app.auth.service;

import com.app.auth.dto.*;
import com.app.auth.entity.UserEntity;
import com.app.auth.enums.Role;
import com.app.auth.mapper.UserMapper;
import com.app.auth.repository.UserRepository;
import com.app.auth.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private JwtTokenService jwtTokenService;
    private JwtUtil jwtUtil;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtTokenService = mock(JwtTokenService.class);
        jwtUtil = mock(JwtUtil.class);

        authService = new AuthService(
                userRepository,
                userMapper,
                passwordEncoder,
                jwtTokenService,
                jwtUtil
        );
    }

    private UserEntity mockUser() {
        UserEntity u = new UserEntity();
        u.setId("u1");
        u.setEmail("a@b.com");
        u.setPassword("enc");
        u.setRole(Role.CUSTOMER);
        return u;
    }

    // ================= REGISTER SUCCESS =================
    @Test
    void registerUser_success() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("a@b.com");
        dto.setPhoneNumber("999");
        dto.setPassword("raw");

        UserEntity user = mockUser();

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(passwordEncoder.encode("raw")).thenReturn("enc");
        when(userRepository.save(any())).thenReturn(user);

        when(jwtTokenService.generateAccessToken(user)).thenReturn("access");
        when(jwtTokenService.generateRefreshToken(user)).thenReturn("refresh");

        when(jwtUtil.getIssuedAt(any())).thenReturn(LocalDateTime.now());
        when(jwtUtil.getExpiresAt(any())).thenReturn(LocalDateTime.now().plusSeconds(60));
        when(userMapper.toProfileDto(any())).thenReturn(new UserProfileResponseDTO());

        AuthResponseDTO response = authService.registerUser(dto);

        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
    }

    // ================= AUTHENTICATE SUCCESS =================
    @Test
    void authenticateUser_success() {
        AuthRequestDTO req = new AuthRequestDTO();
        req.setEmail("a@b.com");
        req.setPassword("raw");

        UserEntity user = mockUser();

        when(userRepository.findByEmail("a@b.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw", "enc"))
                .thenReturn(true);

        when(jwtTokenService.generateAccessToken(user)).thenReturn("access");
        when(jwtTokenService.generateRefreshToken(user)).thenReturn("refresh");

        when(jwtUtil.getIssuedAt(any())).thenReturn(LocalDateTime.now());
        when(jwtUtil.getExpiresAt(any())).thenReturn(LocalDateTime.now().plusSeconds(60));
        when(userMapper.toProfileDto(any())).thenReturn(new UserProfileResponseDTO());

        AuthResponseDTO response = authService.authenticateUser(req);

        assertEquals("access", response.getAccessToken());
    }

    // ================= REFRESH TOKEN SUCCESS =================
    @Test
    void refreshAccessToken_success() {
        JwtTokenDTO stored = new JwtTokenDTO();
        stored.setUserId("u1");

        UserEntity user = mockUser();

        when(jwtTokenService.getTokenFromRedis("refresh"))
                .thenReturn(stored);
        when(userRepository.findById("u1"))
                .thenReturn(Optional.of(user));
        when(jwtTokenService.generateAccessToken(user))
                .thenReturn("newAccess");

        when(jwtUtil.getIssuedAt(any()))
                .thenReturn(LocalDateTime.now());
        when(jwtUtil.getExpiresAt(any()))
                .thenReturn(LocalDateTime.now().plusSeconds(60));
        when(userMapper.toProfileDto(any()))
                .thenReturn(new UserProfileResponseDTO());

        AuthResponseDTO response =
                authService.refreshAccessToken("refresh");

        assertEquals("newAccess", response.getAccessToken());
    }

    // ================= VALIDATE TOKEN =================
    @Test
    void validateToken_success() {
        JwtTokenDTO dto = new JwtTokenDTO();

        when(jwtTokenService.getTokenFromRedis("token"))
                .thenReturn(dto);

        assertEquals(dto, authService.validateToken("token"));
    }

    // ================= LOGOUT =================
    @Test
    void logout_executes() {
        authService.logout("token");
        verify(jwtTokenService).removeTokenFromRedis("token");
    }

    // ================= CHANGE PASSWORD =================
    @Test
    void changePassword_success() {
        UserEntity user = mockUser();

        when(userRepository.findById("u1"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);
        when(passwordEncoder.encode(any()))
                .thenReturn("newEnc");

        authService.changePassword("u1", "old", "new");

        verify(userRepository).save(user);
        verify(jwtTokenService).removeAllTokensForUser("u1");
    }

    // ================= INITIATE PASSWORD RESET =================
    @Test
    void initiatePasswordReset_success() {
        UserEntity user = mockUser();

        when(userRepository.findByEmail("a@b.com"))
                .thenReturn(Optional.of(user));
        when(jwtTokenService.generatePasswordResetToken(any()))
                .thenReturn("reset");

        authService.initiatePasswordReset("a@b.com");

        verify(jwtTokenService)
                .storePasswordResetToken("a@b.com", "reset");
    }

    // ================= RESET PASSWORD =================
    @Test
    void resetPassword_success() {
        UserEntity user = mockUser();

        when(jwtTokenService.validatePasswordResetToken("reset"))
                .thenReturn("a@b.com");
        when(userRepository.findByEmail("a@b.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any()))
                .thenReturn("newEnc");

        authService.resetPassword("reset", "newPwd");

        verify(jwtTokenService).removePasswordResetToken("reset");
        verify(jwtTokenService).removeAllTokensForUser("u1");
    }

    // ================= GET ALL TECHNICIANS =================
    @Test
    void getAllTechnicians_success() {
        UserEntity tech = new UserEntity();
        tech.setId("t1");
        tech.setFirstName("John");
        tech.setRole(Role.TECHNICIAN);

        when(userRepository.findByRole(Role.TECHNICIAN))
                .thenReturn(List.of(tech));

        List<TechnicianSimpleResponse> list =
                authService.getAllTechnicians();

        assertEquals(1, list.size());
        assertEquals("t1", list.get(0).getId());
        assertEquals("John", list.get(0).getName());
    }
}
