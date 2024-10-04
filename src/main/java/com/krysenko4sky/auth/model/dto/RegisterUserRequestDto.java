package com.krysenko4sky.auth.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequestDto {
    @Email(message = "Login must be a valid email")
    private String login;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = ".*[0-9].*", message = "Password must contain at least one digit")
    @Pattern(regexp = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*", message = "Password must contain at least one special character")
    private String password;
}
