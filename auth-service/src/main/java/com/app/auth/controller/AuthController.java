package com.app.auth.controller;

import com.app.auth.dto.*;
import com.app.auth.payload.ApiResponse;
import com.app.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:4200")
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

        return ResponseEntity.ok(success(
                authService.registerUser(dto),
                "User registered successfully"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
            @Valid @RequestBody AuthRequestDTO request) {

        return ResponseEntity.ok(success(
                authService.authenticateUser(request),
                "Login successful"
        ));
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

        return ResponseEntity.ok(success(
                authService.refreshAccessToken(req.getRefreshToken()),
                "Token refreshed"
        ));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<JwtTokenDTO>> validateToken(
            @RequestBody TokenRequest req) {

        return ResponseEntity.ok(success(
                authService.validateToken(req.getToken()),
                "Token valid"
        ));
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
            @RequestHeader("X-USER-ID") String userId,
            @Valid @RequestBody PasswordChangeDTO dto) {

        authService.changePassword(
                userId,
                dto.getCurrentPassword(),
                dto.getNewPassword()
        );
        return ResponseEntity.ok(success(null, "Password changed"));
    }

    // ===== helpers =====
    private <T> ApiResponse<T> success(T data, String msg) {
        ApiResponse<T> api = new ApiResponse<>();
        api.setStatus("SUCCESS");
        api.setMessage(msg);
        api.setData(data);
        return api;
    }

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
}
