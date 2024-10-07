package com.krysenko4sky.controller;

import com.krysenko4sky.logging.LogArguments;
import com.krysenko4sky.model.dto.ExternalProjectDto;
import com.krysenko4sky.service.ExternalProjectService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

import static com.krysenko4sky.controller.UserController.LOG_PATTERN;

@RestController
@Validated
@Slf4j
@LogArguments
@RequestMapping(ExternalProjectController.PROJECTS)
public class ExternalProjectController {

    private static final String EXTERNAL_PROJECTS_CACHE = "external-projects";
    private static final String ID_KEY = "#id";
    private static final String ALL_PROJECTS_KEY = "'allProjects'";
    public static final String PROJECTS = "/external-projects";
    public static final String BY_USER_ID = "/user/{userId}";
    public static final String BY_ID = "/{id}";
    private final ExternalProjectService externalProjectService;

    @Autowired
    public ExternalProjectController(ExternalProjectService externalProjectService) {
        this.externalProjectService = externalProjectService;
    }

    @PostMapping
    @CacheEvict(value = EXTERNAL_PROJECTS_CACHE, key = ALL_PROJECTS_KEY)
    public Mono<ExternalProjectDto> createExternalProject(@Valid @RequestBody ExternalProjectDto project) {
        logMethodInvoked(PROJECTS, "createExternalProject");
        return externalProjectService.createExternalProject(project);
    }

    @GetMapping(BY_ID)
    @Cacheable(value = EXTERNAL_PROJECTS_CACHE, key = ID_KEY)
    public Mono<ExternalProjectDto> getExternalProjectById(@PathVariable UUID id) {
        logMethodInvoked(PROJECTS + BY_ID, "getExternalProjectById");
        return externalProjectService.getExternalProjectById(id);
    }

    @PutMapping(BY_ID)
    @Caching(evict = {
            @CacheEvict(value = EXTERNAL_PROJECTS_CACHE, key = ID_KEY),
            @CacheEvict(value = EXTERNAL_PROJECTS_CACHE, key = ALL_PROJECTS_KEY)
    })
    public Mono<ExternalProjectDto> updateExternalProject(@PathVariable UUID id, @Valid @RequestBody ExternalProjectDto project) {
        logMethodInvoked(PROJECTS + BY_ID, "updateExternalProject");
        return externalProjectService.updateExternalProject(id, project);
    }

    @DeleteMapping(BY_ID)
    @Caching(evict = {
            @CacheEvict(value = EXTERNAL_PROJECTS_CACHE, key = ID_KEY),
            @CacheEvict(value = EXTERNAL_PROJECTS_CACHE, key = ALL_PROJECTS_KEY)
    })
    public Mono<Void> deleteExternalProject(@PathVariable UUID id) {
        logMethodInvoked(PROJECTS + BY_ID, "deleteExternalProject");
        return externalProjectService.deleteExternalProject(id);
    }

    @GetMapping(BY_USER_ID)
    public Flux<ExternalProjectDto> getExternalProjectsByUserId(@PathVariable UUID userId) {
        logMethodInvoked(PROJECTS + BY_USER_ID, "getExternalProjectsByUserId");
        return externalProjectService.getExternalProjectsByUserId(userId);
    }

    @GetMapping
    @Cacheable(value = EXTERNAL_PROJECTS_CACHE, key = ALL_PROJECTS_KEY)
    public Flux<ExternalProjectDto> getAllProjects() {
        logMethodInvoked(PROJECTS, "getAllUsers");
        return externalProjectService.getAllExternalProjects();
    }

    private static void logMethodInvoked(String path, String method) {
        log.info(LOG_PATTERN, path, method);
    }
}
