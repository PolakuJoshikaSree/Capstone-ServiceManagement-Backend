package com.app.auth.dto;

import com.app.auth.enums.AccountStatus;
import com.app.auth.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileResponseDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Role role;
    private AccountStatus accountStatus;
    private Boolean passwordExpired;
    private LocalDateTime lastLoginAt;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private LocalDateTime createdAt;
}

