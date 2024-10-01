package com.krysenko4sky.service.impl;

import com.krysenko4sky.model.ExternalProject;
import com.krysenko4sky.repository.ExternalProjectRepository;
import com.krysenko4sky.service.ExternalProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ExternalProjectServiceImpl implements ExternalProjectService {

    private final ExternalProjectRepository externalProjectRepository;

    @Autowired
    public ExternalProjectServiceImpl(ExternalProjectRepository externalProjectRepository) {
        this.externalProjectRepository = externalProjectRepository;
    }

    @Override
    public Mono<ExternalProject> createExternalProject(ExternalProject project) {
        return externalProjectRepository.save(project);
    }

    @Override
    public Mono<ExternalProject> getExternalProjectById(UUID id) {
        return externalProjectRepository.findById(id);
    }

    @Override
    public Mono<ExternalProject> updateExternalProject(UUID id, ExternalProject project) {
        return externalProjectRepository.findById(id)
                .flatMap(existingProject -> {
                    existingProject.setName(project.getName());
                    return externalProjectRepository.save(existingProject);
                });
    }

    @Override
    public Mono<Void> deleteExternalProject(UUID id) {
        return externalProjectRepository.deleteById(id);
    }

    @Override
    public Flux<ExternalProject> getExternalProjectsByUserId(UUID userId) {
        return externalProjectRepository.findByUserId(userId);
    }
}
