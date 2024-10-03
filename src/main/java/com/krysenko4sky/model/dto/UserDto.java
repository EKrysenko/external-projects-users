package com.krysenko4sky.model.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {

    private UUID id;
    @Email(message = "invalid email")
    private String email;
    private String username;
}
