package com.krysenko4sky.repository;

import com.krysenko4sky.model.dao.ExternalProject;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ExternalProjectRepository extends ReactiveCrudRepository<ExternalProject, UUID> {

    Flux<ExternalProject> findByUserId(UUID userId);
}
