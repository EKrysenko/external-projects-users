package com.krysenko4sky.service;

import com.krysenko4sky.model.ExternalProject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ExternalProjectService {
    Mono<ExternalProject> createExternalProject(ExternalProject project);

    Mono<ExternalProject> getExternalProjectById(UUID id);

    Mono<ExternalProject> updateExternalProject(UUID id, ExternalProject project);

    Mono<Void> deleteExternalProject(UUID id);

    Flux<ExternalProject> getExternalProjectsByUserId(UUID userId);
}
