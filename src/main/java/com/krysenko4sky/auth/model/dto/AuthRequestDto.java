package com.krysenko4sky.auth.model.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String login;
    private String password;
}
