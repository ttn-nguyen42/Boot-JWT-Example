package com.example.authentication_example.unit;

import com.example.authentication_example.AuthenticationExampleApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = AuthenticationExampleApplication.class)
@TestPropertySource(locations = {"classpath:application.properties"})
public class AuthenticationExampleAppTests {

    @Value(value = "${spring.datasource.username}")
    public String memoryDatabaseAccessName;

    @Test
    @DisplayName("can load new application.properties")
    public void testChangeOfDatabase() {
        // Then
        Assertions.assertThat(this.memoryDatabaseAccessName).isEqualTo("sa");
    }
}
