package com.example.authenticationexample.controller.implementation;

import com.example.authenticationexample.controller.SecretApi;
import com.example.authenticationexample.dto.response.SecretResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class SecretController implements SecretApi {
    @Override
    public ResponseEntity<SecretResponseDto> getSecret() {
        SecretResponseDto dto = new SecretResponseDto();
        dto.setKey("This is a secret key");
        return ResponseEntity.ok(dto);
    }
}
