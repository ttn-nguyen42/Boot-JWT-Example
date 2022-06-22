package com.example.authentication_example.dto.response;

import java.util.Objects;

public class SignedInUser {
    private String accessToken;
    private String refreshToken;
    private Long id;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignedInUser that = (SignedInUser) o;
        return Objects.equals(getAccessToken(), that.getAccessToken()) && Objects.equals(getRefreshToken(), that.getRefreshToken()) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccessToken(), getRefreshToken(), getId());
    }

    @Override
    public String toString() {
        return "SignedInUser{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", id=" + id +
                '}';
    }
}
