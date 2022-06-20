package com.example.authenticationexample.service;

import com.example.authenticationexample.dto.request.NewUserDto;
import com.example.authenticationexample.dto.response.SignedInUser;
import com.example.authenticationexample.entity.UserEntity;

import java.util.Optional;

public interface UserService {
    UserEntity findUserByEmail(String email);
    Optional<SignedInUser> createUser(NewUserDto user);
    SignedInUser getSignedInUser(UserEntity userEntity);
    Optional<SignedInUser> getAccessToken(String refreshToken);
    void removeRefreshToken(String refreshToken);
}
