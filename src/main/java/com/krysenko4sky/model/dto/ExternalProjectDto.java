package com.krysenko4sky.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ExternalProjectDto {
    private UUID id;
    private UUID userId;
    @NotBlank
    private String name;
}
