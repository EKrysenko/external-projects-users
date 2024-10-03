package com.krysenko4sky.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ExternalProjectDto {
    private UUID id;
    private UUID userId;
    private String name;
}
