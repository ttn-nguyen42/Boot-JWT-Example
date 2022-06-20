package com.example.authenticationexample.controller.implementation;

import com.example.authenticationexample.controller.AuthApi;
import com.example.authenticationexample.dto.request.NewUserDto;
import com.example.authenticationexample.dto.request.RefreshToken;
import com.example.authenticationexample.dto.response.SignedInUser;
import com.example.authenticationexample.dto.request.CredentialDto;
import com.example.authenticationexample.entity.UserEntity;
import com.example.authenticationexample.exception.InsufficientAuthentication;
import com.example.authenticationexample.exception.InvalidRefreshToken;
import com.example.authenticationexample.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class AuthController implements AuthApi {
    private final UserService service;
    private final PasswordEncoder encoder;

    public AuthController(UserService service, PasswordEncoder encoder) {
        this.service = service;
        this.encoder = encoder;
    }

    @Override
    public ResponseEntity<SignedInUser> register(@Valid NewUserDto newUser) {
        Optional<SignedInUser> createdUser = service.createUser(newUser);
        if (createdUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser.get());
        }
        throw new InsufficientAuthentication("Unauthorized");
    }

    @Override
    public ResponseEntity<SignedInUser> login(@Valid CredentialDto credentials) {
        UserEntity entity = service.findUserByEmail(credentials.getEmail());
        if (encoder.matches(credentials.getPassword(), entity.getPassword())) {
            return ResponseEntity.ok(service.getSignedInUser(entity));
        }
        throw new InsufficientAuthentication("Unauthorized");
    }

    @Override
    public ResponseEntity<Void> logout(RefreshToken token) {
        service.removeRefreshToken(token.getToken());
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<SignedInUser> getAccessToken(RefreshToken token) {
        return ResponseEntity.ok(service.getAccessToken(token.getToken())
                                        .orElseThrow(() -> new InvalidRefreshToken("Invalid refresh token")));
    }

}
