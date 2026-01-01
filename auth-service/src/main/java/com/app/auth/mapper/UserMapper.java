package com.app.auth.mapper;

import com.app.auth.dto.RegisterUserDTO;
import com.app.auth.dto.UserProfileResponseDTO;
import com.app.auth.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public UserEntity toEntity(RegisterUserDTO dto) {
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(dto.getRole());
        user.setAccountStatus(com.app.auth.enums.AccountStatus.ACTIVE);
        user.setPasswordExpired(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public UserProfileResponseDTO toProfileDto(UserEntity entity) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(entity.getId());  
        dto.setEmail(entity.getEmail());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setRole(entity.getRole());
        dto.setAccountStatus(entity.getAccountStatus());
        dto.setPasswordExpired(entity.getPasswordExpired());
        dto.setLastLoginAt(entity.getLastLoginAt());
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setZipCode(entity.getZipCode());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
