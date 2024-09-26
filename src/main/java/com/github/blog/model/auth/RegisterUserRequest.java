package com.github.blog.model.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {

    @Schema(description = "Email address for the new user",
            example = "user@example.com",
            defaultValue = "user@example.com")
    private String email;

    @Schema(description = "Password for the new user account",
            example = "@PasswOrd123",
            defaultValue = "@PasswOrd123")
    private String password;

    @Schema(description = "Full name of the new user",
            example = "John Doe",
            defaultValue = "John Doe")
    private String fullName;
}
