package com.krysenko4sky.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Table("users")
@Data
public class User {
    @Id
    private UUID id;
    private String email;
    private String password;
    private String name;
    private List<Role> roles;
}
