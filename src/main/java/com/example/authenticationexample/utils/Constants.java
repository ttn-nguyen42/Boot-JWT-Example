package com.example.authenticationexample.utils;

public class Constants {
    public static final String ENCODER = "bcrypt";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String SECRET_KEY = "SECRET_KEY";
    public static final long EXPIRATION_TIME = 900_000;
    public static final String ROLE_CLAIM = "roles";
    public static final String AUTHORITY_PRIFIX = "ROLE_";

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";

    public static final String LOGIN_PATH = "/api/v1/auth/login";
    public static final String REGISTER_PATH = "/api/v1/auth/register";
    public static final String LOGOUT_PATH = "/api/v1/auth/logout";
    public static final String TOKEN_PATH = "/api/v1/auth/token";
}
