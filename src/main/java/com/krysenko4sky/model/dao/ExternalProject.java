package com.krysenko4sky.model.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table("external_projects")
@Data
public class ExternalProject {
    @Id
    private UUID id;
    private UUID userId;
    private String name;

}
