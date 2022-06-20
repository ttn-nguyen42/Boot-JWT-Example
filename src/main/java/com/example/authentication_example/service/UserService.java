package com.example.authentication_example.service;

import com.example.authentication_example.dto.request.NewUserDto;
import com.example.authentication_example.dto.response.SignedInUser;
import com.example.authentication_example.entity.UserEntity;

import java.util.Optional;

public interface UserService {
    UserEntity findUserByEmail(String email);
    Optional<SignedInUser> createUser(NewUserDto user);
    SignedInUser getSignedInUser(UserEntity userEntity);
    Optional<SignedInUser> getAccessToken(String refreshToken);
    void removeRefreshToken(String refreshToken);
}
