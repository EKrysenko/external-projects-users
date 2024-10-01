package com.krysenko4sky.controller;

import com.krysenko4sky.model.ExternalProject;
import com.krysenko4sky.service.ExternalProjectService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/external-projects")
public class ExternalProjectController {

    private final ExternalProjectService externalProjectService;

    @Autowired
    public ExternalProjectController(ExternalProjectService externalProjectService) {
        this.externalProjectService = externalProjectService;
    }

    @PostMapping
    public Mono<ExternalProject> createExternalProject(@RequestBody ExternalProject project) {
        return externalProjectService.createExternalProject(project);
    }

    @GetMapping("/{id}")
    public Mono<ExternalProject> getExternalProjectById(@PathVariable UUID id) {
        return externalProjectService.getExternalProjectById(id);
    }

    @PutMapping("/{id}")
    public Mono<ExternalProject> updateExternalProject(@PathVariable UUID id, @RequestBody ExternalProject project) {
        return externalProjectService.updateExternalProject(id, project);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteExternalProject(@PathVariable UUID id) {
        return externalProjectService.deleteExternalProject(id);
    }

    @GetMapping("/user/{userId}")
    public Flux<ExternalProject> getExternalProjectsByUserId(@PathVariable UUID userId) {
        return externalProjectService.getExternalProjectsByUserId(userId);
    }
}
