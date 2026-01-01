package com.app.auth.entity;

import com.app.auth.enums.AccountStatus;
import com.app.auth.enums.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;   // 

    @Indexed(unique = true)
    private String email;

    private String password;
    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String phoneNumber;

    private Role role;
    private AccountStatus accountStatus;

    private Boolean passwordExpired;
    private LocalDateTime passwordChangedAt;
    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String address;
    private String city;
    private String state;
    private String zipCode;
}
