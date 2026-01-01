package com.app.auth.dto;

import com.app.auth.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleDTO {
    @NotNull(message = "Role is required")
    private Role role;
}

