package com.example.authentication_example.unit.service;

import com.example.authentication_example.config.JwtManager;
import com.example.authentication_example.dto.request.NewUserDto;
import com.example.authentication_example.dto.response.SignedInUser;
import com.example.authentication_example.entity.UserEntity;
import com.example.authentication_example.exception.EmailNotFound;
import com.example.authentication_example.exception.UserAlreadyExist;
import com.example.authentication_example.model.RoleEnum;
import com.example.authentication_example.repository.UserRepository;
import com.example.authentication_example.repository.UserTokenRepository;
import com.example.authentication_example.service.UserService;
import com.example.authentication_example.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder bCryptEncoder;

    @Mock
    private JwtManager tokenManager;

    private NewUserDto mockNewUserDto;
    private UserEntity mockEntity;
    private SignedInUser mockSignedInUser;

    @BeforeEach
    void setup() {
        service = new UserServiceImpl(userRepository, tokenRepository, bCryptEncoder, tokenManager);

        mockEntity = new UserEntity();
        mockEntity.setId(1L);
        mockEntity.setEmail("ttn.nguyen42@gmail.com");
        mockEntity.setPassword("EncryptedPassword");
        mockEntity.setRole(RoleEnum.USER);

        mockNewUserDto = new NewUserDto();
        mockNewUserDto.setEmail("ttn.nguyen42@gmail.com");
        mockNewUserDto.setName("Trung Nguyen");
        mockNewUserDto.setPassword("nguyen132");

        mockSignedInUser = new SignedInUser();
        mockSignedInUser.setId(1L);
        mockSignedInUser.setRefreshToken("RefreshToken");
        mockSignedInUser.setAccessToken("AccessToken");
    }

    @Test
    @DisplayName("returns correct user with correct email")
    public void testFindByEmail_CorrectEmail() {
        // Given
        given(userRepository.findByEmail("ttn.nguyen42@gmail.com")).willReturn(Optional.of(mockEntity));

        // When
        UserEntity res = service.findUserByEmail("ttn.nguyen42@gmail.com");

        // Then
        assertThat(res).isEqualTo(mockEntity);
    }

    @Test
    @DisplayName("throws EmailNotFound when user not found")
    public void testFindByEmail_NotFoundEmail() {
        // Given
        given(userRepository.findByEmail("ttn.nguyen42@gmail.com")).willReturn(Optional.empty());

        try {
            // When
            service.findUserByEmail("ttn.nguyen42@gmail.com");
        } catch (Exception e) {
            // Then
            assertThat(e).isInstanceOf(EmailNotFound.class);
            assertThat(e.getMessage()).contains("User not found");
        }
    }

    @Test
    @DisplayName("throws EmailNotFound when passed in invalid email")
    public void testFindByEmail_InvalidEmail() {
        try {
            // When
            service.findUserByEmail("");
        } catch (Exception e) {
            // Then
            assertThat(e).isInstanceOf(EmailNotFound.class);
            assertThat(e.getMessage()).contains("Invalid email");
        }
        verify(userRepository, times(0)).findByEmail("");
    }

    @Test
    @DisplayName("can create user with full information")
    public void testCreateUser_CorrectInformation() {
        UserEntity toEntityResult = new UserEntity(mockNewUserDto.getEmail(), mockEntity.getPassword());

        // Given
        given(userRepository.findByEmail(mockNewUserDto.getEmail())).willReturn(Optional.empty());
        given(userRepository.save(toEntityResult)).willReturn(mockEntity);
        given(bCryptEncoder.encode(mockNewUserDto.getPassword())).willReturn(mockEntity.getPassword());
        given(tokenManager.create(any(UserDetails.class))).willReturn("AccessToken");

        // When
        Optional<SignedInUser> res = service.createUser(mockNewUserDto);

        // Then
        assertThat(res).isPresent();
        assertThat(res.get().getRefreshToken()).isNotEmpty();
        assertThat(res.get().getAccessToken()).isEqualTo("AccessToken");
    }

    @Test
    @DisplayName("throws UserAlreadyExist when register with used email")
    public void testCreateUser_UsedEmail() {
        // Given
        given(userRepository.findByEmail(mockNewUserDto.getEmail())).willReturn(Optional.of(mockEntity));
        try {
            // When
            service.createUser(mockNewUserDto);
        } catch (Exception e) {
            // Then
            assertThat(e).isInstanceOf(UserAlreadyExist.class);
            assertThat(e.getMessage()).isEqualTo("Email already in use");
        }
        verify(userRepository, times(0)).save(any());
    }
}
