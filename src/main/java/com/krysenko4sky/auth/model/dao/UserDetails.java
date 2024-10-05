package com.krysenko4sky.auth.model.dao;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("user_details")
@Data
@Builder
public class UserDetails {
    @Id
    private UUID id;
    private String email;
    private String password;
    private String refreshToken;
}
