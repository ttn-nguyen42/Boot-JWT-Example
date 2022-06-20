package com.example.authentication_example.controller.implementation;

import com.example.authentication_example.controller.SecretApi;
import com.example.authentication_example.dto.response.SecretResponseDto;
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
