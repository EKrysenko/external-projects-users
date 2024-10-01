package com.krysenko4sky.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("external_projects")
@Data
public class ExternalProject {
    @Id
    private UUID id;
    private Long userId;
    private String name;
}
