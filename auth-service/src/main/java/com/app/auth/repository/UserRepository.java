package com.app.auth.repository;

import com.app.auth.entity.UserEntity;
import com.app.auth.enums.AccountStatus;
import com.app.auth.enums.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    List<UserEntity> findByRole(Role role);
    List<UserEntity> findByAccountStatus(AccountStatus status);
}
