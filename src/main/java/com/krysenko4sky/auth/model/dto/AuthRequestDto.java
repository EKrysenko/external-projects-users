package com.krysenko4sky.auth.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequestDto {
    private String login;
    private String password;
}
