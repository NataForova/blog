package com.github.blog.model.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @Schema(description = "Email address of the user",
            example = "user@example.com",
            defaultValue = "user@example.com")
    private String email;
    @Schema(description = "Password for the user account",
            example = "@PasswOrd123",
            defaultValue = "@PasswOrd123")
    private String password;
}