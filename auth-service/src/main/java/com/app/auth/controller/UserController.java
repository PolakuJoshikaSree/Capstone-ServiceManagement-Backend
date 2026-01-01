package com.app.auth.controller;

import com.app.auth.dto.*;
import com.app.auth.payload.ApiResponse;
import com.app.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> getProfile(
            @RequestHeader("X-USER-ID") String userId) {

        return ok("Profile fetched", userService.getUserProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> updateProfile(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody UpdateUserProfileDTO dto) {

        return ok("Profile updated", userService.updateUserProfile(userId, dto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> getUserById(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String userId) {

        requireAnyRole(roles, "ROLE_ADMIN", "ROLE_MANAGER");
        return ok("User fetched", userService.getUserProfile(userId));
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> updateStatus(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String userId,
            @RequestBody UpdateAccountStatusDTO dto) {

        requireRole(roles, "ROLE_ADMIN");
        return ok("Status updated",
                userService.updateAccountStatus(userId, dto.getAccountStatus()));
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> updateRole(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String userId,
            @RequestBody UpdateRoleDTO dto) {

        requireRole(roles, "ROLE_ADMIN");
        return ok("Role updated",
                userService.updateUserRole(userId, dto.getRole()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-USER-ROLES") String roles,
            @PathVariable String userId) {

        requireRole(roles, "ROLE_ADMIN");
        userService.deleteUser(userId);
        return ok("User deleted", null);
    }

    // ===== helpers =====
    private void requireRole(String roles, String role) {
        if (!roles.contains(role)) throw new SecurityException("Forbidden");
    }

    private void requireAnyRole(String roles, String... allowed) {
        for (String r : allowed)
            if (roles.contains(r)) return;
        throw new SecurityException("Forbidden");
    }

    private <T> ResponseEntity<ApiResponse<T>> ok(String msg, T data) {
        ApiResponse<T> api = new ApiResponse<>();
        api.setStatus("SUCCESS");
        api.setMessage(msg);
        api.setData(data);
        return ResponseEntity.ok(api);
    }
}
