package com.app.auth.service;

import com.app.auth.dto.UpdateUserProfileDTO;
import com.app.auth.dto.UserProfileResponseDTO;
import com.app.auth.entity.UserEntity;
import com.app.auth.enums.AccountStatus;
import com.app.auth.enums.Role;
import com.app.auth.mapper.UserMapper;
import com.app.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    UserRepository userRepository = mock(UserRepository.class);
    UserMapper userMapper = mock(UserMapper.class);

    UserService service = new UserService(userRepository, userMapper);

    @Test
    void getUserProfile_success() {
        UserEntity user = new UserEntity();
        UserProfileResponseDTO dto = new UserProfileResponseDTO();

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userMapper.toProfileDto(user)).thenReturn(dto);

        assertEquals(dto, service.getUserProfile("1"));
    }

    @Test
    void updateUserProfile_success() {
        UserEntity user = new UserEntity();
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        UserProfileResponseDTO resp = new UserProfileResponseDTO();

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toProfileDto(user)).thenReturn(resp);

        assertEquals(resp, service.updateUserProfile("1", dto));
    }

    @Test
    void updateAccountStatus_success() {
        UserEntity user = new UserEntity();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toProfileDto(user)).thenReturn(new UserProfileResponseDTO());

        assertNotNull(service.updateAccountStatus("1", AccountStatus.ACTIVE));
    }

    @Test
    void updateUserRole_success() {
        UserEntity user = new UserEntity();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toProfileDto(user)).thenReturn(new UserProfileResponseDTO());

        assertNotNull(service.updateUserRole("1", Role.ADMIN));
    }

    @Test
    void deleteUser_setsInactive() {
        UserEntity user = new UserEntity();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        service.deleteUser("1");

        assertEquals(AccountStatus.INACTIVE, user.getAccountStatus());
    }

    @Test
    void getAllUsers_success() {
        UserEntity user = new UserEntity();
        Page<UserEntity> page =
                new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1);

        when(userRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(userMapper.toProfileDto(user)).thenReturn(new UserProfileResponseDTO());

        assertEquals(1, service.getAllUsers(0, 10, "", "").getTotalElements());
    }
}
