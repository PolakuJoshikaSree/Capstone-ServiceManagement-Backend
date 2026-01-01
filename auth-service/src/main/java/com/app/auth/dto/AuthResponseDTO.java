package com.app.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private String accessToken;
    private String tokenType; // "Bearer"
    private Long expiresIn; // seconds
    private String refreshToken;
    private UserProfileResponseDTO user;

    // indicates that the returned token is restricted because the user's password is expired
    private boolean passwordExpired;
}
