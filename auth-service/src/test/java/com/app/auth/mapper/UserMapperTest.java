package com.app.auth.mapper;

import com.app.auth.dto.UserProfileResponseDTO;
import com.app.auth.entity.UserEntity;
import com.app.auth.enums.AccountStatus;
import com.app.auth.enums.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toProfileDto_success() {
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setEmail("a@b.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("999");
        user.setRole(Role.CUSTOMER);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setPasswordExpired(false);
        user.setAddress("Street");
        user.setCity("City");
        user.setState("State");
        user.setZipCode("12345");
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLoginAt(LocalDateTime.now());

        UserProfileResponseDTO dto = mapper.toProfileDto(user);

        assertEquals("1", dto.getId());
        assertEquals("a@b.com", dto.getEmail());
        assertEquals("Test", dto.getFirstName());
        assertEquals("User", dto.getLastName());
        assertEquals("999", dto.getPhoneNumber());
        assertEquals(Role.CUSTOMER, dto.getRole());
        assertEquals(AccountStatus.ACTIVE, dto.getAccountStatus());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getLastLoginAt());
    }
    
}
