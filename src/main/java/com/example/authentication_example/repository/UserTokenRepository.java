package com.example.authentication_example.repository;

import com.example.authentication_example.entity.UserTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserTokenRepository extends CrudRepository<UserTokenEntity, Long> {
    Optional<UserTokenEntity> findByRefreshToken(String refreshToken);
    Optional<UserTokenEntity> deleteByUserId(Long id);
}
