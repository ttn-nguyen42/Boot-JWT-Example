package com.example.authentication_example.unit.controller;

import com.example.authentication_example.AppConfig;
import com.example.authentication_example.advice.RestApiErrorHandler;
import com.example.authentication_example.controller.implementation.SecretController;
import com.example.authentication_example.dto.response.SecretResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@JsonTest
public class SecretControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private SecretController controller;

    private JacksonTester<SecretResponseDto> json;

    @BeforeEach
    public void setup() {
        ObjectMapper mapper = new AppConfig().objectMapper();
        JacksonTester.initFields(this, mapper);
        MappingJackson2HttpMessageConverter mappingConverter = new MappingJackson2HttpMessageConverter();
        mappingConverter.setObjectMapper(mapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .setControllerAdvice(new RestApiErrorHandler())
                                 .setMessageConverters(mappingConverter)
                                 .build();
    }

    @Test
    @DisplayName("returns correct user secret")
    public void testUserSecret() throws Exception {
        // When
        MockHttpServletResponse res = mockMvc.perform(get("/api/v1/secret")
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .accept(MediaType.APPLICATION_JSON))
                                             .andDo(MockMvcResultHandlers.print())
                                             .andReturn()
                                             .getResponse();
        // Then
        assertThat(res.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(this.json.parseObject(res.getContentAsString()).getKey())
                .isEqualTo("This is a secret key");
    }

    @Test
    @DisplayName("returns correct admin secret")
    public void testAdminSecret() throws Exception {
        // When
        MockHttpServletResponse res = mockMvc.perform(get("/api/v1/secret/admin").contentType(MediaType.APPLICATION_JSON)
                                                                                 .accept(MediaType.APPLICATION_JSON))
                                             .andDo(MockMvcResultHandlers.print())
                                             .andReturn()
                                             .getResponse();
        // Then
        assertThat(res.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(this.json.parseObject(res.getContentAsString()).getKey()).isEqualTo("Admin secret key");
    }
}
