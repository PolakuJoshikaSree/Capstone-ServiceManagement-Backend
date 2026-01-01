package com.app.auth.controller;

import com.app.auth.dto.UpdateAccountStatusDTO;
import com.app.auth.dto.UpdateRoleDTO;
import com.app.auth.dto.UpdateUserProfileDTO;
import com.app.auth.dto.UserProfileResponseDTO;
import com.app.auth.payload.ApiResponse;
import com.app.auth.payload.PageResponse;
import com.app.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> getProfile() {
        String userId = getCurrentUserId();
        return ok("User profile fetched", userService.getUserProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> updateProfile(
            @RequestBody UpdateUserProfileDTO dto) {

        String userId = getCurrentUserId();
        return ok("Profile updated", userService.updateUserProfile(userId, dto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> getUserById(
            @PathVariable String userId) {

        requireAnyRole("ROLE_ADMIN", "ROLE_MANAGER");
        return ok("User fetched", userService.getUserProfile(userId));
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> updateStatus(
            @PathVariable String userId,
            @RequestBody UpdateAccountStatusDTO dto) {

        requireRole("ROLE_ADMIN");
        return ok("Account status updated",
                userService.updateAccountStatus(userId, dto.getAccountStatus()));
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> updateRole(
            @PathVariable String userId,
            @RequestBody UpdateRoleDTO dto) {

        requireRole("ROLE_ADMIN");
        return ok("User role updated",
                userService.updateUserRole(userId, dto.getRole()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String userId) {
        requireRole("ROLE_ADMIN");
        userService.deleteUser(userId);
        return ok("User deleted", null);
    }

    // ===== helpers =====

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return String.valueOf(auth.getPrincipal());
    }

    private void requireRole(String role) {
        if (!roles().contains(role)) throw new SecurityException("Forbidden");
    }

    private void requireAnyRole(String... roles) {
        for (String r : roles)
            if (roles().contains(r)) return;
        throw new SecurityException("Forbidden");
    }

    private Set<String> roles() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    private <T> ResponseEntity<ApiResponse<T>> ok(String msg, T data) {
        ApiResponse<T> api = new ApiResponse<>();
        api.setStatus("SUCCESS");
        api.setMessage(msg);
        api.setData(data);
        return ResponseEntity.ok(api);
    }
}
