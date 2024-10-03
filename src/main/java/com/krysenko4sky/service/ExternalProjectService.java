package com.krysenko4sky.service;

import com.krysenko4sky.model.dto.ExternalProjectDto;
import com.krysenko4sky.model.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ExternalProjectService {
    Mono<ExternalProjectDto> createExternalProject(ExternalProjectDto project);

    Mono<ExternalProjectDto> getExternalProjectById(UUID id);

    Mono<ExternalProjectDto> updateExternalProject(UUID id, ExternalProjectDto project);

    Mono<Void> deleteExternalProject(UUID id);

    Flux<ExternalProjectDto> getExternalProjectsByUserId(UUID userId);

    Flux<ExternalProjectDto> getAllExternalProjects();
}
