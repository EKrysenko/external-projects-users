package com.krysenko4sky.controller;

import com.krysenko4sky.exception.ProjectNotFoundException;
import com.krysenko4sky.exception.UserNotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void canCreateExternalProject() {
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

        verify(externalProjectService).createExternalProject(any());
    }

    @Test
    void createExternalProject_FailsOnEmptyName() {
        webTestClient.post()
                .uri(ExternalProjectController.PROJECTS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"\"}")
                .exchange()
                .expectStatus().isBadRequest();

        verify(externalProjectService, times(0)).createExternalProject(any());
    }

    @Test
    void canGetExternalProjectById() {
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

        verify(externalProjectService).getExternalProjectById(projectId);
    }

    @Test
    void getExternalProjectById_FailsOnNotFound() {
        UUID nonExistentProjectId = UUID.randomUUID();

        when(externalProjectService.getExternalProjectById(nonExistentProjectId))
                .thenReturn(Mono.error(new ProjectNotFoundException(nonExistentProjectId)));

        webTestClient.get()
                .uri(ExternalProjectController.PROJECTS + "/{id}", nonExistentProjectId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        verify(externalProjectService).getExternalProjectById(nonExistentProjectId);
    }

    @Test
    void canUpdateExternalProject() {
        UUID projectId = UUID.randomUUID();
        ExternalProjectDto projectDto = ExternalProjectDto.builder()
                .id(projectId)
                .name("Updated Project")
                .build();

        when(externalProjectService.updateExternalProject(eq(projectId), any())).thenReturn(Mono.just(projectDto));

        webTestClient.put()
                .uri(ExternalProjectController.PROJECTS + "/{id}", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"Updated Project\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated Project");

        verify(externalProjectService).updateExternalProject(eq(projectId), any());
    }

    @Test
    void updateExternalProject_FailsOnProjectNotFound() {
        UUID nonExistentProjectId = UUID.randomUUID();

        when(externalProjectService.updateExternalProject(eq(nonExistentProjectId), any()))
                .thenReturn(Mono.error(new ProjectNotFoundException(nonExistentProjectId)));

        webTestClient.put()
                .uri(ExternalProjectController.PROJECTS + "/{id}", nonExistentProjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"Updated Project\"}")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(response -> assertEquals("Project with id: '" + nonExistentProjectId + "' not exist.", response));

        verify(externalProjectService).updateExternalProject(eq(nonExistentProjectId), any());
    }

    @Test
    void updateExternalProject_FailsOnUserNotFound() {
        UUID projectId = UUID.randomUUID();
        UUID nonExistentUserId = UUID.randomUUID();
        ExternalProjectDto externalProjectDto = ExternalProjectDto.builder()
                .name("test project")
                .userId(nonExistentUserId)
                .build();

        when(externalProjectService.updateExternalProject(eq(projectId), eq(externalProjectDto)))
                .thenReturn(Mono.error(new UserNotFoundException(nonExistentUserId)));

        webTestClient.put()
                .uri(ExternalProjectController.PROJECTS + "/{id}", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(externalProjectDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(response -> assertEquals("User with id: '" + nonExistentUserId + "' not exist.", response));

        verify(externalProjectService).updateExternalProject(eq(projectId), any());
    }

    @Test
    void canDeleteExternalProject() {
        UUID projectId = UUID.randomUUID();

        when(externalProjectService.deleteExternalProject(projectId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(ExternalProjectController.PROJECTS + "/{id}", projectId)
                .exchange()
                .expectStatus().isOk();

        verify(externalProjectService).deleteExternalProject(projectId);
    }

    @Test
    void canGetExternalProjectsByUserId() {
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

        verify(externalProjectService).getExternalProjectsByUserId(userId);
    }

    @Test
    void getExternalProjectsByUserId_EmptyOnUserWithoutProjects() {
        UUID userId = UUID.randomUUID();

        when(externalProjectService.getExternalProjectsByUserId(userId))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri(ExternalProjectController.PROJECTS + "/user/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("[]");

        verify(externalProjectService).getExternalProjectsByUserId(userId);
    }

    @Test
    void canGetAllProjects() {
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

        verify(externalProjectService).getAllExternalProjects();
    }
}
