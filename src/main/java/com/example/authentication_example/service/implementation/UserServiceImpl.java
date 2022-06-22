package com.example.authentication_example.service.implementation;

import com.example.authentication_example.config.JwtManager;
import com.example.authentication_example.dto.request.NewUserDto;
import com.example.authentication_example.dto.response.SignedInUser;
import com.example.authentication_example.entity.UserEntity;
import com.example.authentication_example.entity.UserTokenEntity;
import com.example.authentication_example.exception.EmailNotFound;
import com.example.authentication_example.exception.InvalidRefreshToken;
import com.example.authentication_example.exception.UserAlreadyExist;
import com.example.authentication_example.repository.UserRepository;
import com.example.authentication_example.repository.UserTokenRepository;
import com.example.authentication_example.service.UserService;
import com.example.authentication_example.utils.RandomHolder;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserTokenRepository tokenRepository;
    private final PasswordEncoder bCryptEncoder;
    private final JwtManager tokenManager;

    public UserServiceImpl(UserRepository userRepository,
                           UserTokenRepository tokenRepository,
                           PasswordEncoder bCryptEncoder,
                           JwtManager tokenManager) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.bCryptEncoder = bCryptEncoder;
        this.tokenManager = tokenManager;
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        if (email.isEmpty()) {
            throw new EmailNotFound("Invalid email");
        }
        final String mail = email.trim();
        Optional<UserEntity> resultEntity = userRepository.findByEmail(mail);
        return resultEntity.orElseThrow(() -> new EmailNotFound("User not found"));
    }

    @Override
    @Transactional
    public Optional<SignedInUser> createUser(NewUserDto newUserDto) {
        Optional<UserEntity> resultEntity = userRepository.findByEmail(newUserDto.getEmail());
        if (resultEntity.isPresent()) {
            throw new UserAlreadyExist("Email already in use");
        }
        UserEntity newEntity = userRepository.save(toEntity(newUserDto));
        return Optional.of(createSignedInUserWithRefreshToken(newEntity));
    }

    @Override
    @Transactional
    public SignedInUser getSignedInUser(UserEntity userEntity) {
        tokenRepository.deleteByUserId(userEntity.getId());
        return createSignedInUserWithRefreshToken(userEntity);
    }

    @Override
    @Transactional
    public Optional<SignedInUser> getAccessToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken).map(tokenEntity -> {
            SignedInUser signedInUser = createSignedInUser(tokenEntity.getUser());
            signedInUser.setRefreshToken(refreshToken);
            return Optional.of(signedInUser);
        }).orElseThrow(() -> new InvalidRefreshToken("Invalid refresh token"));
    }

    @Override
    public void removeRefreshToken(String refreshToken) {
        tokenRepository.findByRefreshToken(refreshToken).ifPresentOrElse(tokenRepository::delete, () -> {
            throw new InvalidRefreshToken("Invalid refresh token");
        });
    }

    private UserEntity toEntity(NewUserDto dto) {
        return new UserEntity(dto.getEmail(), bCryptEncoder.encode(dto.getPassword()));
    }

    private SignedInUser createSignedInUser(UserEntity user) {
        String token = tokenManager.create(User.builder()
                                               .username(user.getEmail())
                                               .password(user.getPassword())
                                               .authorities(Objects.nonNull(user.getRole()) ? user.getRole().name() : "")
                                               .build());
        SignedInUser signedInUser = new SignedInUser();
        signedInUser.setId(user.getId());
        signedInUser.setAccessToken(token);
        return signedInUser;
    }

    private SignedInUser createSignedInUserWithRefreshToken(UserEntity user) {
        SignedInUser signedInUser = createSignedInUser(user);
        signedInUser.setRefreshToken(createRefreshToken(user));
        return signedInUser;
    }

    private String createRefreshToken(UserEntity user) {
        String token = RandomHolder.randomKey(128);
        UserTokenEntity tokenEntity = new UserTokenEntity();
        tokenEntity.setRefreshToken(token);
        tokenEntity.setUser(user);
        tokenRepository.save(tokenEntity);
        return token;
    }
}
