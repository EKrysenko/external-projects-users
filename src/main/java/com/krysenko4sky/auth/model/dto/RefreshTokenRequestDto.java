package com.krysenko4sky.auth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDto {

    @NotBlank(message = "Access token cannot be blank")
    private String accessToken;
}
