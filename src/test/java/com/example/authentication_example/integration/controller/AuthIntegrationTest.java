package com.example.authentication_example.integration.controller;

import com.example.authentication_example.dto.other.ExceptionResult;
import com.example.authentication_example.dto.request.CredentialDto;
import com.example.authentication_example.dto.request.NewUserDto;
import com.example.authentication_example.dto.request.RefreshToken;
import com.example.authentication_example.dto.response.SignedInUser;
import com.example.authentication_example.integration.utils.IntegrationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = {"classpath:application.properties"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthIntegrationTest {
    private static Logger log = LoggerFactory.getLogger(AuthIntegrationTest.class);
    private static ObjectMapper mapper;

    private static JacksonTester<ExceptionResult> exceptionTester;

    private NewUserDto registrationDto;
    private MultiValueMap<String, String> postHeader;
    private CredentialDto loginDto;
    private RefreshToken tokenDto;

    @Autowired
    private TestRestTemplate template;

    @BeforeAll
    public static void init() {
        mapper = IntegrationUtils.mapper();
    }

    @BeforeEach
    public void setup() {
        registrationDto = new NewUserDto();
        registrationDto.setEmail("ttn.nguyen42@gmail.com");
        registrationDto.setPassword("nguyen132");
        registrationDto.setName("Trung Nguyen");

        loginDto = new CredentialDto();
        loginDto.setEmail("ttn.nguyen42@gmail.com");
        loginDto.setPassword("nguyen132");

        tokenDto = new RefreshToken();
        tokenDto.setToken("RefreshToken");

        postHeader = new LinkedMultiValueMap<>();
        postHeader.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        postHeader.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        postHeader.add(HttpHeaders.ACCEPT_ENCODING, "utf-8");
        JacksonTester.initFields(this, mapper);
    }

    @Test
    @DisplayName("rejects registration with missing information")
    @Order(1)
    public void testRegister_MissingInformation() {
        // Given
        registrationDto.setName(null);

        // When
        ResponseEntity<ExceptionResult> res = template.exchange("/api/v1/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(registrationDto, postHeader),
                ExceptionResult.class);

        if (res.hasBody()) {
            log.info(Objects.requireNonNull(res.getBody()).toString());
        }

        // Then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(res.getBody())
                          .getMessage()).containsAnyOf("Name must not be null", "Name must not be empty");
    }

    @Test
    @DisplayName("returns access and refresh key after register")
    @Order(2)
    public void testRegister_Correct() {
        // When
        ResponseEntity<SignedInUser> res = template.exchange("/api/v1/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(registrationDto, postHeader),
                SignedInUser.class);

        if (res.hasBody()) {
            log.info(Objects.requireNonNull(res.getBody()).toString());
        }
        // Then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(Objects.requireNonNull(res.getBody()).getAccessToken()).isNotEmpty();
        assertThat(res.getBody().getRefreshToken()).isNotEmpty();
    }

    @Test
    @DisplayName("rejects registration with existing email")
    @Order(3)
    public void testRegister_Conflict() {
        // When
        ResponseEntity<ExceptionResult> res = template.exchange("/api/v1/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(registrationDto, postHeader),
                ExceptionResult.class);

        if (res.hasBody()) {
            log.info(Objects.requireNonNull(res.getBody()).toString());
        }
        // Then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(Objects.requireNonNull(res.getBody()).getMessage()).contains("Email already in use");
    }

    @Test
    @DisplayName("rejects login with missing credentials")
    @Order(4)
    public void testLogin_MissingInformation() {
        // Given
        loginDto.setPassword(null);

        // When
        ResponseEntity<ExceptionResult> res = template.exchange("/api/v1/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginDto, postHeader),
                ExceptionResult.class);

        if (res.hasBody()) {
            log.info(Objects.requireNonNull(res.getBody()).toString());
        }

        // Then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(res.getBody())
                          .getMessage()).containsAnyOf("Password must not be empty", "Password must not be null");
    }

    @Test
    @DisplayName("rejects login with incorrect credentials")
    @Order(5)
    public void testLogin_Incorrect() {
        // Given
        loginDto.setPassword("IncorrectPassword");

        // When
        ResponseEntity<ExceptionResult> res = template.exchange("/api/v1/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginDto, postHeader),
                ExceptionResult.class);

        if (res.hasBody()) {
            log.info(Objects.requireNonNull(res.getBody()).toString());
        }

        // Then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(Objects.requireNonNull(res.getBody()).getMessage()).containsAnyOf("Unauthorized");
    }

    @Test
    @DisplayName("returns access and refresh key after login")
    @Order(6)
    public void testLogin_Correct() {
        // When
        ResponseEntity<SignedInUser> res = template.exchange("/api/v1/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginDto, postHeader),
                SignedInUser.class);

        if (res.hasBody()) {
            log.info(Objects.requireNonNull(res.getBody()).toString());
        }

        // Then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(res.getBody()).getAccessToken()).isNotEmpty();
        assertThat(res.getBody().getRefreshToken()).isNotEmpty();
    }

    @Test
    @DisplayName("rejects logouts with incorrect refresh token")
    @Order(7)
    public void testLogout_InvalidTokens() {
        // When
        ResponseEntity<ExceptionResult> res = template.exchange("/api/v1/auth/logout",
                HttpMethod.DELETE,
                new HttpEntity<>(tokenDto, postHeader),
                ExceptionResult.class);

        // Then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(Objects.requireNonNull(res.getBody()).getMessage()).contains("Invalid refresh token");
    }

    @Test
    @DisplayName("accepts logout request of proper refresh token")
    @Order(8)
    public void testLogout_Correct() {
        // Given
        ResponseEntity<SignedInUser> loginRes = template.exchange("/api/v1/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginDto, postHeader),
                SignedInUser.class);
        tokenDto.setToken(Objects.requireNonNull(loginRes.getBody()).getRefreshToken());

        // When
        ResponseEntity<Void> res = template.exchange("/api/v1/auth/logout",
                HttpMethod.DELETE,
                new HttpEntity<>(tokenDto, postHeader),
                Void.class);

        // Then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(res.hasBody()).isEqualTo(false);
    }

    @Test
    @DisplayName("rejects invalid refresh token for renew tokens")
    @Order(9)
    public void testRefresh_InvalidToken() {
        // When
        ResponseEntity<ExceptionResult> res = template.exchange("/api/v1/auth/token",
                HttpMethod.POST,
                new HttpEntity<>(tokenDto, postHeader),
                ExceptionResult.class);

        // Then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(Objects.requireNonNull(res.getBody()).getMessage()).contains("Invalid refresh token");
    }

    @Test
    @DisplayName("returns new access token and refresh token when refresh")
    @Order(10)
    public void testRefresh_Correct() {
        // Given
        ResponseEntity<SignedInUser> loginRes = template.exchange("/api/v1/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginDto, postHeader),
                SignedInUser.class);
        tokenDto.setToken(Objects.requireNonNull(loginRes.getBody()).getRefreshToken());
        String refreshKey = String.copyValueOf(loginRes.getBody().getRefreshToken().toCharArray());
        String accessKey = String.copyValueOf(loginRes.getBody().getAccessToken().toCharArray());

        // When
        ResponseEntity<SignedInUser> res = template.exchange("/api/v1/auth/token",
                HttpMethod.POST,
                new HttpEntity<>(tokenDto, postHeader),
                SignedInUser.class);

        // Then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.hasBody()).isEqualTo(true);
        assertThat(refreshKey).isEqualTo(Objects.requireNonNull(res.getBody()).getRefreshToken());
        assertThat(accessKey).isNotEqualTo(res.getBody().getAccessToken());
    }
}
