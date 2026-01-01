package com.app.auth.dto;

import com.app.auth.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JwtTokenDTO {
    private String token;
    private String email;
    private Role role;
    private String userId;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
}

