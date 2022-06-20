package com.example.authenticationexample.repository;

import com.example.authenticationexample.entity.UserTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserTokenRepository extends CrudRepository<UserTokenEntity, Long> {
    Optional<UserTokenEntity> findByRefreshToken(String refreshToken);
    Optional<UserTokenEntity> deleteByUserId(Long id);
}
