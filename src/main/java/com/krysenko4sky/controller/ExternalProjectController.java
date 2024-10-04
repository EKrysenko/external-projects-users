package com.krysenko4sky.controller;

import com.krysenko4sky.model.dto.ExternalProjectDto;
import com.krysenko4sky.service.ExternalProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    private static final String EXTERNAL_PROJECTS_CACHE = "external-projects";
    private static final String ID_KEY = "#id";
    private static final String ALL_PROJECTS_KEY = "'allProjects'";
    private final ExternalProjectService externalProjectService;

    @Autowired
    public ExternalProjectController(ExternalProjectService externalProjectService) {
        this.externalProjectService = externalProjectService;
    }

    @PostMapping
    @CacheEvict(value = EXTERNAL_PROJECTS_CACHE, key = ALL_PROJECTS_KEY)
    public Mono<ExternalProjectDto> createExternalProject(@Valid @RequestBody ExternalProjectDto project) {
        return externalProjectService.createExternalProject(project);
    }

    @GetMapping("/{id}")
    @Cacheable(value = EXTERNAL_PROJECTS_CACHE, key = ID_KEY)
    public Mono<ExternalProjectDto> getExternalProjectById(@PathVariable UUID id) {
        return externalProjectService.getExternalProjectById(id);
    }

    @PutMapping("/{id}")
    @Caching(evict = {
            @CacheEvict(value = EXTERNAL_PROJECTS_CACHE, key = ID_KEY),
            @CacheEvict(value = EXTERNAL_PROJECTS_CACHE, key = ALL_PROJECTS_KEY)
    })    public Mono<ExternalProjectDto> updateExternalProject(@PathVariable UUID id, @Valid @RequestBody ExternalProjectDto project) {
        return externalProjectService.updateExternalProject(id, project);
    }

    @DeleteMapping("/{id}")
    @Caching(evict = {
            @CacheEvict(value = EXTERNAL_PROJECTS_CACHE, key = ID_KEY),
            @CacheEvict(value = EXTERNAL_PROJECTS_CACHE, key = ALL_PROJECTS_KEY)
    })
    public Mono<Void> deleteExternalProject(@PathVariable UUID id) {
        return externalProjectService.deleteExternalProject(id);
    }

    @GetMapping("/user/{userId}")
    public Flux<ExternalProjectDto> getExternalProjectsByUserId(@PathVariable UUID userId) {
        return externalProjectService.getExternalProjectsByUserId(userId);
    }

    @GetMapping
    @Cacheable(value = EXTERNAL_PROJECTS_CACHE, key = ALL_PROJECTS_KEY)
    public Flux<ExternalProjectDto> getAllUsers() {
        return externalProjectService.getAllExternalProjects();
    }
}
