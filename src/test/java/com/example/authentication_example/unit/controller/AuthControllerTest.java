package com.example.authentication_example.unit.controller;

import com.example.authentication_example.AppConfig;
import com.example.authentication_example.advice.RestApiErrorHandler;
import com.example.authentication_example.controller.implementation.AuthController;
import com.example.authentication_example.dto.other.ExceptionResult;
import com.example.authentication_example.dto.request.CredentialDto;
import com.example.authentication_example.dto.request.NewUserDto;
import com.example.authentication_example.dto.response.SignedInUser;
import com.example.authentication_example.entity.UserEntity;
import com.example.authentication_example.model.RoleEnum;
import com.example.authentication_example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@JsonTest
public class AuthControllerTest {
    private Logger log = LoggerFactory.getLogger(AuthControllerTest.class);

    private AuthController controller;

    private MockMvc mockMvc;

    @Mock
    private UserService service;

    @Mock
    private PasswordEncoder encoder;

    private static SignedInUser testSignedInUser;
    private static UserEntity testEntity;

    private JacksonTester<SignedInUser> signedInTester;
    private JacksonTester<NewUserDto> dtoTester;
    private JacksonTester<CredentialDto> credentialTester;
    private JacksonTester<ExceptionResult> exceptionResultTester;

    @BeforeEach
    public void setup() {
        controller = new AuthController(service, encoder);
        ObjectMapper mapper = new AppConfig().objectMapper();
        JacksonTester.initFields(this, mapper);
        MappingJackson2HttpMessageConverter mappingConverter = new MappingJackson2HttpMessageConverter();
        mappingConverter.setObjectMapper(mapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .setControllerAdvice(new RestApiErrorHandler())
                                 .setMessageConverters(mappingConverter)
                                 .build();
        initializeTestVariables();
    }

    private void initializeTestVariables() {
        testSignedInUser = new SignedInUser();
        testSignedInUser.setId(1L);
        testSignedInUser.setRefreshToken("RefreshToken");
        testSignedInUser.setAccessToken("AccessToken");

        testEntity = new UserEntity();
        testEntity.setId(1L);
        testEntity.setEmail("ttn.nguyen42@gmail.com");
        testEntity.setPassword("EncryptedPassword");
        testEntity.setRole(RoleEnum.USER);
    }

    @Test
    @DisplayName("returns keys when registers with full information")
    public void testRegister_WithFullInformation() throws Exception {
        NewUserDto dto = new NewUserDto();
        dto.setEmail("ttn.nguyen42@gmail.com");
        dto.setPassword("nguyen132");
        dto.setName("Trung Nguyen");
        // Given
        given(service.createUser(dto)).willReturn(Optional.of(testSignedInUser));

        // When
        MockHttpServletResponse res = mockMvc.perform(post("/api/v1/auth/register")
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .content(dtoTester.write(dto).getJson()).characterEncoding("utf-8").accept(MediaType.APPLICATION_JSON))
                                             .andDo(MockMvcResultHandlers.print())
                                             .andReturn()
                                             .getResponse();
        // Then
        assertThat(res.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(res.getContentAsString()).isEqualTo(signedInTester.write(testSignedInUser).getJson());
    }

    @Test
    @DisplayName("returns correct access key with existing user")
    public void testLogin_WithCorrectInformation() throws Exception {
        CredentialDto dto = new CredentialDto();
        dto.setEmail("ttn.nguyen42@gmail.com");
        dto.setPassword("nguyen132");

        // Given
        given(service.findUserByEmail(dto.getEmail())).willReturn(testEntity);
        given(encoder.matches(dto.getPassword(), testEntity.getPassword())).willReturn(true);
        given(service.getSignedInUser(testEntity)).willReturn(testSignedInUser);

        // When
        MockHttpServletResponse res = mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                                                                                .accept(MediaType.APPLICATION_JSON)
                                                                                .characterEncoding("utf-8")
                                                                                .content(credentialTester.write(dto)
                                                                                                         .getJson()))
                                             .andReturn()
                                             .getResponse();

        // Then
        assertThat(res.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(signedInTester.parseObject(res.getContentAsByteArray())).isEqualTo(testSignedInUser);
    }

    @Test
    @DisplayName("returns Unauthorized code when login with incorrect password")
    public void testLogin_WithIncorrectInformation() throws Exception {
        CredentialDto dto = new CredentialDto();
        dto.setEmail("ttn.nguyen42@gmail.com");
        dto.setPassword("nguyen132");

        // Given
        given(service.findUserByEmail(dto.getEmail())).willReturn(testEntity);
        given(encoder.matches(dto.getPassword(), testEntity.getPassword())).willReturn(false);

        MockHttpServletResponse res = mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                                                                                .accept(MediaType.APPLICATION_JSON)
                                                                                .characterEncoding("utf-8")
                                                                                .content(credentialTester.write(dto)
                                                                                                         .getJson()))
                                             .andReturn()
                                             .getResponse();

        // Then
        assertThat(res.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResultTester.parseObject(res.getContentAsByteArray())
                                        .getMessage()).isEqualTo("Unauthorized");
        verify(service, times(0)).getSignedInUser(testEntity);
    }
}
