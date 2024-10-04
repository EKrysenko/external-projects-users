package com.krysenko4sky.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserDto {

    private UUID id;
    private String username;
}
