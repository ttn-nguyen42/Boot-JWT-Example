package com.example.authenticationexample.controller;

import com.example.authenticationexample.dto.response.SecretResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/secret")
public interface SecretApi {
    @GetMapping
    ResponseEntity<SecretResponseDto> getSecret();
}
