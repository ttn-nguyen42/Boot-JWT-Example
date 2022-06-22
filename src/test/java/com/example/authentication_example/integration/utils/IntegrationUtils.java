package com.example.authentication_example.integration.utils;

import com.example.authentication_example.AppConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

public class IntegrationUtils {
    private static ObjectMapper mapper;

    public static boolean isTokenExpired(String jwt) throws JsonProcessingException {
        String encodedPayload = jwt.split("\\.")[1];
        String payload = new String(Base64.getDecoder().decode(encodedPayload));
        JsonNode parent = new ObjectMapper().readTree(payload);
        String expiration = parent.path("exp").asText();
        Instant expTime = Instant.ofEpochMilli(Long.parseLong(expiration) * 1000);
        return Instant.now().compareTo(expTime) < 0;
    }

    public static ObjectMapper mapper() {
        if (Objects.isNull(mapper)) {
            mapper = new AppConfig().objectMapper();
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        }
        return mapper;
    }
}
