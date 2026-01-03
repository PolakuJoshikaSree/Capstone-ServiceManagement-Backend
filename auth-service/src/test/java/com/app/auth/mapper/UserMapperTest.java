package com.app.auth.mapper;

import com.app.auth.dto.RegisterUserDTO;
import com.app.auth.dto.UserProfileResponseDTO;
import com.app.auth.entity.UserEntity;
import com.app.auth.enums.AccountStatus;
import com.app.auth.enums.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toEntity_mapsAllFieldsCorrectly() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("test@mail.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPhoneNumber("+919999999999");
        dto.setRole(Role.CUSTOMER);

        UserEntity entity = userMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("test@mail.com", entity.getEmail());
        assertEquals("John", entity.getFirstName());
        assertEquals("Doe", entity.getLastName());
        assertEquals("+919999999999", entity.getPhoneNumber());
        assertEquals(Role.CUSTOMER, entity.getRole());

        // defaults set inside mapper
        assertEquals(AccountStatus.ACTIVE, entity.getAccountStatus());
        assertFalse(entity.getPasswordExpired());

        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void toProfileDto_mapsAllFieldsCorrectly() {
        UserEntity entity = new UserEntity();
        entity.setId("123");
        entity.setEmail("user@mail.com");
        entity.setFirstName("Jane");
        entity.setLastName("Smith");
        entity.setPhoneNumber("+918888888888");
        entity.setRole(Role.MANAGER);
        entity.setAccountStatus(AccountStatus.ACTIVE);
        entity.setPasswordExpired(false);
        entity.setLastLoginAt(LocalDateTime.now().minusDays(1));
        entity.setAddress("Street 1");
        entity.setCity("Hyderabad");
        entity.setState("Telangana");
        entity.setZipCode("500001");
        entity.setCreatedAt(LocalDateTime.now().minusDays(10));

        UserProfileResponseDTO dto = userMapper.toProfileDto(entity);

        assertNotNull(dto);
        assertEquals("123", dto.getId());
        assertEquals("user@mail.com", dto.getEmail());
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals("+918888888888", dto.getPhoneNumber());
        assertEquals(Role.MANAGER, dto.getRole());
        assertEquals(AccountStatus.ACTIVE, dto.getAccountStatus());
        assertFalse(dto.getPasswordExpired());
        assertEquals(entity.getLastLoginAt(), dto.getLastLoginAt());
        assertEquals("Street 1", dto.getAddress());
        assertEquals("Hyderabad", dto.getCity());
        assertEquals("Telangana", dto.getState());
        assertEquals("500001", dto.getZipCode());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
    }
}
