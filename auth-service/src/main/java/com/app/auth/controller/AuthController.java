package com.app.auth.controller;

import com.app.auth.dto.*;
import com.app.auth.payload.ApiResponse;
import com.app.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(
            @Valid @RequestBody RegisterUserDTO dto) {

        AuthResponseDTO res = authService.registerUser(dto);
        return ResponseEntity.ok(success(res, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
            @Valid @RequestBody AuthRequestDTO request) {

        AuthResponseDTO res = authService.authenticateUser(request);
        return ResponseEntity.ok(success(res, "Login successful"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }
        return ResponseEntity.ok(success(null, "Logged out"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(
            @RequestBody RefreshTokenRequest req) {

        AuthResponseDTO res = authService.refreshAccessToken(req.getRefreshToken());
        return ResponseEntity.ok(success(res, "Token refreshed"));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<JwtTokenDTO>> validateToken(
            @RequestBody TokenRequest req) {

        JwtTokenDTO dto = authService.validateToken(req.getToken());
        return ResponseEntity.ok(success(dto, "Token valid"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO dto) {

        authService.initiatePasswordReset(dto.getEmail());
        return ResponseEntity.ok(success(null, "If email exists, reset token sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordDTO dto) {

        authService.resetPassword(dto.getResetToken(), dto.getNewPassword());
        return ResponseEntity.ok(success(null, "Password reset successful"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody PasswordChangeDTO dto) {

        String userId = String.valueOf(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()
        );

        authService.changePassword(
                userId,
                dto.getCurrentPassword(),
                dto.getNewPassword()
        );
        return ResponseEntity.ok(success(null, "Password changed"));
    }

    // ===== INNER DTOs =====
    public static class RefreshTokenRequest {
        private String refreshToken;
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    public static class TokenRequest {
        private String token;
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    private <T> ApiResponse<T> success(T data, String msg) {
        ApiResponse<T> api = new ApiResponse<>();
        api.setStatus("SUCCESS");
        api.setMessage(msg);
        api.setData(data);
        return api;
    }
}
