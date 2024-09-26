package com.github.blog;

import com.github.blog.exception.EmailAlreadyInUseException;
import com.github.blog.exception.InvalidEmailException;
import com.github.blog.exception.PasswordConstraintException;
import com.github.blog.model.User;
import com.github.blog.model.auth.RegisterUserRequest;
import com.github.blog.model.auth.Role;
import com.github.blog.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
public class AuthServiceTest {
    private final String TEST_EMAIL = "test1@gmail.com";
    private final String TEST_EMAIL_2 = "test2@gmail.com";
    private final String TEST_EMAIL_INVALID = "test1gmail.com";
    private final String TEST_NAME = "test positive";
    private final String VALID_PASSWORD = "@!tTest123";
    private final String INVALID_PASSWORD = "test123";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private AuthService authService;

    @Test
    void singupPositiveTest() {
        var registerUserRequest = getLoginRequest(TEST_EMAIL, TEST_NAME, VALID_PASSWORD);
        var expectedResponse = new User(1L,
                TEST_NAME,
                TEST_EMAIL,
                VALID_PASSWORD,
                Set.of(new Role(2L, "ROLE_USER")),
                LocalDateTime.now(),
                Collections.emptyList());
        var createdUser = authService.signup(registerUserRequest);
        assertThat(createdUser.getUsername()).isEqualTo(expectedResponse.getUsername());
        assertThat(createdUser.getEmail()).isEqualTo(expectedResponse.getEmail());
        assertThat(createdUser.getRoles().size()).isEqualTo(1);
        var role = createdUser.getRoles();
        assertThat(role.iterator().next().getName()).isEqualTo("ROLE_AUTHOR");
    }

    @Test
    void singupWhenEmailAlreadyContainsTest() {
        var registerUserRequest = getLoginRequest(TEST_EMAIL_2, TEST_NAME, VALID_PASSWORD);
        authService.signup(registerUserRequest);
        var registerOneMoreUserRequest = getLoginRequest(TEST_EMAIL_2, TEST_NAME, VALID_PASSWORD);
        try {
            authService.signup(registerOneMoreUserRequest);
            fail();
        } catch (RuntimeException e) {
            assertThat(e).isInstanceOf(EmailAlreadyInUseException.class);
        }
    }

    @Test
    void singupWhenIncorrectPasswordTest() {
        var registerUserRequest = getLoginRequest(TEST_EMAIL_2, TEST_NAME, INVALID_PASSWORD);
        try {
            authService.signup(registerUserRequest);
            fail();
        } catch (RuntimeException e) {
            assertThat(e).isInstanceOf(PasswordConstraintException.class);
        }
    }

    @Test
    void singupWhenIncorrectEmailTest() {
        var registerUserRequest = getLoginRequest(TEST_EMAIL_INVALID, TEST_NAME, VALID_PASSWORD);
        try {
            authService.signup(registerUserRequest);
            fail();
        } catch (RuntimeException e) {
            assertThat(e).isInstanceOf(InvalidEmailException.class);
        }
    }

    private RegisterUserRequest getLoginRequest(String email, String name, String password) {
        return new RegisterUserRequest(email,
                password,
                name);
    }




}
