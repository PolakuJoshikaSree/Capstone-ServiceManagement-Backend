package com.app.auth.dto;

import com.app.auth.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAccountStatusDTO {
    @NotNull(message = "Account status is required")
    private AccountStatus accountStatus;

    private String reason; // Optional reason for status change
}

