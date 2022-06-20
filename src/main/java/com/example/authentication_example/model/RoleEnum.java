package com.example.authentication_example.model;

import com.example.authentication_example.utils.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.security.core.GrantedAuthority;

public enum RoleEnum implements GrantedAuthority {
    USER(Constants.USER),
    ADMIN(Constants.ADMIN);

    private final String authority;

    RoleEnum(String authority) {
        this.authority = authority;
    }

    @Override
    @JsonValue
    public String getAuthority() {
        return authority;
    }

    @JsonCreator
    public static RoleEnum fromAuthority(String authority) {
        for (RoleEnum b : RoleEnum.values()) {
            if (b.authority.equals(authority)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected authority " + authority);
    }

    @Override
    public String toString() {
        return String.valueOf(authority);
    }
}

