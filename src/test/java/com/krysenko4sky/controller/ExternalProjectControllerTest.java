package com.krysenko4sky.controller;

import com.krysenko4sky.model.dto.ExternalProjectDto;
import com.krysenko4sky.service.ExternalProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(ExternalProjectController.class)
@Import(TestSecurityConfig.class)
class ExternalProjectControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ExternalProjectService externalProjectService;

    @Test
    void createExternalProject() {
        ExternalProjectDto projectDto = ExternalProjectDto.builder()
                .id(UUID.randomUUID())
                .name("Test Project")
                .build();

        when(externalProjectService.createExternalProject(any())).thenReturn(Mono.just(projectDto));

        webTestClient.post()
                .uri(ExternalProjectController.PROJECTS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"Test Project\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Test Project");

        verify(externalProjectService, times(1)).createExternalProject(any());
    }

    @Test
    void getExternalProjectById() {
        UUID projectId = UUID.randomUUID();
        ExternalProjectDto projectDto = ExternalProjectDto.builder()
                .id(projectId)
                .name("Test Project")
                .build();

        when(externalProjectService.getExternalProjectById(projectId)).thenReturn(Mono.just(projectDto));

        webTestClient.get()
                .uri(ExternalProjectController.PROJECTS + "/{id}", projectId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Test Project");

        verify(externalProjectService, times(1)).getExternalProjectById(projectId);
    }

    @Test
    void updateExternalProject() {
        UUID projectId = UUID.randomUUID();
        ExternalProjectDto projectDto = ExternalProjectDto.builder()
                .id(projectId)
                .name("Updated Project")
                .build();

        when(externalProjectService.updateExternalProject(eq(projectId), any())).thenReturn(Mono.just(projectDto));

        webTestClient.put()
                .uri(ExternalProjectController.PROJECTS + "/{id}", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"Updated Project\"}") // Настройте JSON по вашим нуждам
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated Project");

        verify(externalProjectService, times(1)).updateExternalProject(eq(projectId), any());
    }

    @Test
    void deleteExternalProject() {
        UUID projectId = UUID.randomUUID();

        when(externalProjectService.deleteExternalProject(projectId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(ExternalProjectController.PROJECTS + "/{id}", projectId)
                .exchange()
                .expectStatus().isOk();

        verify(externalProjectService, times(1)).deleteExternalProject(projectId);
    }

    @Test
    void getExternalProjectsByUserId() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        ExternalProjectDto projectDto = ExternalProjectDto.builder()
                .id(projectId)
                .name("User Project")
                .build();

        when(externalProjectService.getExternalProjectsByUserId(userId)).thenReturn(Flux.just(projectDto));

        webTestClient.get()
                .uri(ExternalProjectController.PROJECTS + "/user/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("User Project");

        verify(externalProjectService, times(1)).getExternalProjectsByUserId(userId);
    }

    @Test
    void getAllProjects() {
        ExternalProjectDto projectDto = ExternalProjectDto.builder()
                .id(UUID.randomUUID())
                .name("All Projects")
                .build();

        when(externalProjectService.getAllExternalProjects()).thenReturn(Flux.just(projectDto));

        webTestClient.get()
                .uri(ExternalProjectController.PROJECTS)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("All Projects");

        verify(externalProjectService, times(1)).getAllExternalProjects();
    }
}
