package com.example.authentication_example.dto.response;

import javax.validation.constraints.NotEmpty;

public class SecretResponseDto {
    @NotEmpty
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
