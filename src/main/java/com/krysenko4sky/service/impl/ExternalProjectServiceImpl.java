package com.krysenko4sky.service.impl;

import com.google.common.base.Preconditions;
import com.krysenko4sky.exception.ProjectNotFoundException;
import com.krysenko4sky.exception.UserNotFoundException;
import com.krysenko4sky.logging.LogArguments;
import com.krysenko4sky.mapper.ExternalProjectMapper;
import com.krysenko4sky.model.dto.ExternalProjectDto;
import com.krysenko4sky.repository.ExternalProjectRepository;
import com.krysenko4sky.repository.UserRepository;
import com.krysenko4sky.service.ExternalProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@LogArguments
public class ExternalProjectServiceImpl implements ExternalProjectService {

    private final ExternalProjectRepository externalProjectRepository;
    private final ExternalProjectMapper externalProjectMapper;
    private final UserRepository userRepository;

    @Autowired
    public ExternalProjectServiceImpl(ExternalProjectRepository externalProjectRepository, UserRepository userRepository,
                                      ExternalProjectMapper externalProjectMapper) {
        this.externalProjectRepository = externalProjectRepository;
        this.userRepository = userRepository;
        this.externalProjectMapper = externalProjectMapper;
    }

    @Override
    public Mono<ExternalProjectDto> createExternalProject(ExternalProjectDto dto) {
        Preconditions.checkArgument(dto.getId() == null, "Field 'id' must be empty");
        return externalProjectRepository.save(externalProjectMapper.toDao(dto))
                .map(externalProjectMapper::toDto);
    }

    @Override
    public Mono<ExternalProjectDto> getExternalProjectById(UUID id) {
        return externalProjectRepository.findById(id)
                .map(externalProjectMapper::toDto)
                .switchIfEmpty(Mono.error(new ProjectNotFoundException(id)));
    }

    @Override
    public Mono<ExternalProjectDto> updateExternalProject(UUID id, ExternalProjectDto dto) {
        Preconditions.checkArgument(dto.getId().equals(id), "id in path and in dto must be the same");
        UUID userId = dto.getUserId();
        if (userId == null) {
            return externalProjectRepository.findById(id)
                    .switchIfEmpty(Mono.error(new ProjectNotFoundException(id)))
                    .flatMap(existingProject -> externalProjectRepository.save(externalProjectMapper.toDao(dto)))
                    .map(externalProjectMapper::toDto);
        }
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .then(externalProjectRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ProjectNotFoundException(id)))
                        .flatMap(existingProject -> externalProjectRepository.save(externalProjectMapper.toDao(dto)))
                        .map(externalProjectMapper::toDto));
    }

    @Override
    public Mono<Void> deleteExternalProject(UUID id) {
        return externalProjectRepository.deleteById(id)
                .switchIfEmpty(Mono.error(new ProjectNotFoundException(id)));
    }

    @Override
    public Flux<ExternalProjectDto> getExternalProjectsByUserId(UUID userId) {
        return externalProjectRepository.findByUserId(userId).map(externalProjectMapper::toDto);
    }

    @Override
    public Flux<ExternalProjectDto> getAllExternalProjects() {
        return externalProjectRepository.findAll().map(externalProjectMapper::toDto);
    }
}
