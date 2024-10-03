package com.krysenko4sky.controller;

import com.krysenko4sky.model.dto.ExternalProjectDto;
import com.krysenko4sky.model.dto.UserDto;
import com.krysenko4sky.service.ExternalProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/external-projects")
public class ExternalProjectController {

    private final ExternalProjectService externalProjectService;

    @Autowired
    public ExternalProjectController(ExternalProjectService externalProjectService) {
        this.externalProjectService = externalProjectService;
    }

    @PostMapping
    public Mono<ExternalProjectDto> createExternalProject(@Valid @RequestBody ExternalProjectDto project) {
        return externalProjectService.createExternalProject(project);
    }

    @GetMapping("/{id}")
    @Cacheable(value = "external-projects", key = "#id")
    public Mono<ExternalProjectDto> getExternalProjectById(@PathVariable UUID id) {
        return externalProjectService.getExternalProjectById(id);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "external-projects", key = "#id")
    public Mono<ExternalProjectDto> updateExternalProject(@PathVariable UUID id, @Valid @RequestBody ExternalProjectDto project) {
        return externalProjectService.updateExternalProject(id, project);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "external-projects", key = "#id")
    public Mono<Void> deleteExternalProject(@PathVariable UUID id) {
        return externalProjectService.deleteExternalProject(id);
    }

    @GetMapping("/user/{userId}")
    @Cacheable(value = "external-projects-by-user", key = "#userId")
    public Flux<ExternalProjectDto> getExternalProjectsByUserId(@PathVariable UUID userId) {
        return externalProjectService.getExternalProjectsByUserId(userId);
    }

    @GetMapping
    public Flux<ExternalProjectDto> getAllUsers() {
        return externalProjectService.getAllExternalProjects();
    }
}
