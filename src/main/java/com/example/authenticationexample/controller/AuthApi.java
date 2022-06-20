package com.example.authenticationexample.controller;

import com.example.authenticationexample.dto.request.NewUserDto;
import com.example.authenticationexample.dto.request.RefreshToken;
import com.example.authenticationexample.dto.response.SignedInUser;
import com.example.authenticationexample.dto.request.CredentialDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/v1/auth")
public interface AuthApi {
    @PostMapping("/register")
    ResponseEntity<SignedInUser> register(@RequestBody NewUserDto newUser);

    @PostMapping("/login")
    ResponseEntity<SignedInUser> login(@RequestBody CredentialDto credentials);

    @DeleteMapping ("/logout")
    ResponseEntity<Void> logout(@RequestBody RefreshToken token);

    @PostMapping("/token")
    ResponseEntity<SignedInUser> getAccessToken(@RequestBody RefreshToken token);
}
