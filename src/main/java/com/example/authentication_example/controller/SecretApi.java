package com.example.authentication_example.controller;

import com.example.authentication_example.dto.response.SecretResponseDto;
import com.example.authentication_example.utils.Constants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/secret")
public interface SecretApi {
    @GetMapping
    ResponseEntity<SecretResponseDto> getSecret();

    @GetMapping("/admin")
    ResponseEntity<SecretResponseDto> getAdminSecret();
}
