package com.example.authentication_example.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RefreshToken {
    @NotNull(message = "Token must not be null")
    @NotEmpty(message = "Token must not be empty")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
