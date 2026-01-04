package com.app.auth.service;

import com.app.auth.dto.UpdateUserProfileDTO;
import com.app.auth.dto.UserProfileResponseDTO;
import com.app.auth.entity.UserEntity;
import com.app.auth.enums.AccountStatus;
import com.app.auth.enums.Role;
import com.app.auth.mapper.UserMapper;
import com.app.auth.payload.PageResponse;
import com.app.auth.repository.UserRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserProfileResponseDTO getUserProfile(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userMapper.toProfileDto(user);
    }

    public UserProfileResponseDTO updateUserProfile(String userId, UpdateUserProfileDTO dto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setCity(dto.getCity());
        user.setState(dto.getState());
        user.setZipCode(dto.getZipCode());
        user.setUpdatedAt(java.time.LocalDateTime.now());

        return userMapper.toProfileDto(userRepository.save(user));
    }

    public UserProfileResponseDTO updateAccountStatus(String userId, AccountStatus status) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setAccountStatus(status);
        return userMapper.toProfileDto(userRepository.save(user));
    }

    public UserProfileResponseDTO updateUserRole(String userId, Role role) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRole(role);
        return userMapper.toProfileDto(userRepository.save(user));
    }

    public void deleteUser(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setAccountStatus(AccountStatus.INACTIVE);
        userRepository.save(user);
    }

    public PageResponse<UserProfileResponseDTO> getAllUsers(
            int page, int size, String sortBy, String sortDir) {

        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        var p = userRepository.findAll(pageable);

        var content = p.getContent()
                .stream()
                .map(userMapper::toProfileDto)
                .toList();

        PageResponse<UserProfileResponseDTO> resp = new PageResponse<>();
        resp.setContent(content);
        resp.setPage(p.getNumber());
        resp.setSize(p.getSize());
        resp.setTotalElements(p.getTotalElements());
        resp.setTotalPages(p.getTotalPages());
        return resp;
    }
    public List<UserProfileResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toProfileDto) // ✅ FIXED
                .toList();
    }

}
