package com.app.auth.controller;

import com.app.auth.dto.*;
import com.app.auth.dto.TechnicianSimpleResponse;
import com.app.auth.payload.ApiResponse;
import com.app.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(
            @Valid @RequestBody RegisterUserDTO dto) {

        return ResponseEntity.ok(success(
                authService.registerUser(dto),
                "User registered successfully"
        ));
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
            @Valid @RequestBody AuthRequestDTO request) {

        return ResponseEntity.ok(success(
                authService.authenticateUser(request),
                "Login successful"
        ));
    }

    // ================= LOGOUT =================
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }
        return ResponseEntity.ok(success(null, "Logged out"));
    }

    // ================= REFRESH TOKEN =================
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(
            @RequestBody RefreshTokenRequest req) {

        return ResponseEntity.ok(success(
                authService.refreshAccessToken(req.getRefreshToken()),
                "Token refreshed"
        ));
    }

    // ================= VALIDATE TOKEN =================
    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<JwtTokenDTO>> validateToken(
            @RequestBody TokenRequest req) {

        return ResponseEntity.ok(success(
                authService.validateToken(req.getToken()),
                "Token valid"
        ));
    }

    // ================= PASSWORD =================
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

    // ================= TECHNICIANS =================
    @GetMapping("/users/technicians")
    public ResponseEntity<ApiResponse<List<TechnicianSimpleResponse>>> getTechnicians() {

        return ResponseEntity.ok(success(
                authService.getAllTechnicians(),
                "Technicians fetched successfully"
        ));
    }

    // ================= helpers =================
    private <T> ApiResponse<T> success(T data, String msg) {
        ApiResponse<T> api = new ApiResponse<>();
        api.setStatus("SUCCESS");
        api.setMessage(msg);
        api.setData(data);
        return api;
    }

    // ===== inner DTOs =====
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
