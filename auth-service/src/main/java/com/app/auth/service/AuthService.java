package com.app.auth.service;

import com.app.auth.dto.*;
import com.app.auth.entity.UserEntity;
import com.app.auth.mapper.UserMapper;
import com.app.auth.repository.UserRepository;
import com.app.auth.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder,
                       JwtTokenService jwtTokenService,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.jwtUtil = jwtUtil;
    }

    // ================= REGISTER =================
    public AuthResponseDTO registerUser(RegisterUserDTO registerDTO) {

        if (userRepository.existsByEmail(registerDTO.getEmail()))
            throw new IllegalArgumentException("Email already in use");

        if (userRepository.existsByPhoneNumber(registerDTO.getPhoneNumber()))
            throw new IllegalArgumentException("Phone number already in use");

        UserEntity user = userMapper.toEntity(registerDTO);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user = userRepository.save(user);

        return generateTokens(user);
    }

    // ================= LOGIN =================
    public AuthResponseDTO authenticateUser(AuthRequestDTO authRequest) {

        UserEntity user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("Invalid credentials");

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return generateTokens(user);
    }

    // ================= REFRESH TOKEN =================
    public AuthResponseDTO refreshAccessToken(String refreshToken) {

        JwtTokenDTO stored = jwtTokenService.getTokenFromRedis(refreshToken);
        if (stored == null)
            throw new IllegalArgumentException("Invalid refresh token");

        UserEntity user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newAccess = jwtTokenService.generateAccessToken(user);
        JwtTokenDTO accessDto = buildTokenDto(user, newAccess);
        jwtTokenService.storeTokenInRedis(newAccess, accessDto);

        AuthResponseDTO response = new AuthResponseDTO();
        response.setAccessToken(newAccess);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(
                Duration.between(accessDto.getIssuedAt(), accessDto.getExpiresAt()).getSeconds()
        );
        response.setUser(userMapper.toProfileDto(user));
        return response;
    }

    // ================= VALIDATE TOKEN =================
    public JwtTokenDTO validateToken(String token) {
        jwtUtil.validateToken(token);
        JwtTokenDTO dto = jwtTokenService.getTokenFromRedis(token);
        if (dto == null)
            throw new IllegalArgumentException("Token expired or invalid");
        return dto;
    }

    // ================= LOGOUT =================
    public void logout(String token) {
        jwtTokenService.removeTokenFromRedis(token);
    }

    // ================= CHANGE PASSWORD =================
    public void changePassword(String userId, String currentPassword, String newPassword) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword()))
            throw new IllegalArgumentException("Current password incorrect");

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        jwtTokenService.removeAllTokensForUser(user.getId());
    }

    // ================= FORGOT / RESET PASSWORD =================
    public void initiatePasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = jwtTokenService.generatePasswordResetToken(email);
            jwtTokenService.storePasswordResetToken(email, token);
        });
    }

    public void resetPassword(String resetToken, String newPassword) {

        String email = jwtTokenService.validatePasswordResetToken(resetToken);
        if (email == null)
            throw new IllegalArgumentException("Invalid reset token");

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        jwtTokenService.removePasswordResetToken(resetToken);
        jwtTokenService.removeAllTokensForUser(user.getId());
    }

    // ================= HELPERS =================
    private AuthResponseDTO generateTokens(UserEntity user) {

        String access = jwtTokenService.generateAccessToken(user);
        String refresh = jwtTokenService.generateRefreshToken(user);

        JwtTokenDTO accessDto = buildTokenDto(user, access);
        JwtTokenDTO refreshDto = buildTokenDto(user, refresh);

        jwtTokenService.storeTokenInRedis(access, accessDto);
        jwtTokenService.storeTokenInRedis(refresh, refreshDto);

        AuthResponseDTO response = new AuthResponseDTO();
        response.setAccessToken(access);
        response.setRefreshToken(refresh);
        response.setTokenType("Bearer");
        response.setExpiresIn(
                Duration.between(accessDto.getIssuedAt(), accessDto.getExpiresAt()).getSeconds()
        );
        response.setUser(userMapper.toProfileDto(user));
        return response;
    }

    private JwtTokenDTO buildTokenDto(UserEntity user, String token) {
        JwtTokenDTO dto = new JwtTokenDTO();
        dto.setToken(token);
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setUserId(user.getId());
        dto.setIssuedAt(jwtUtil.getIssuedAt(token));
        dto.setExpiresAt(jwtUtil.getExpiresAt(token));
        return dto;
    }
}
