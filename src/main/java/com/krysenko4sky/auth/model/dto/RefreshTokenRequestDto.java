package com.krysenko4sky.auth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RefreshTokenRequestDto {

    @NotBlank(message = "Access token cannot be blank")
    private String accessToken;
}
