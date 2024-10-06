package com.krysenko4sky.service.impl;

import com.krysenko4sky.exception.ProjectNotFoundException;
import com.krysenko4sky.exception.UserNotFoundException;
import com.krysenko4sky.mapper.ExternalProjectMapper;
import com.krysenko4sky.model.dao.ExternalProject;
import com.krysenko4sky.model.dao.User;
import com.krysenko4sky.model.dto.ExternalProjectDto;
import com.krysenko4sky.repository.ExternalProjectRepository;
import com.krysenko4sky.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ExternalProjectServiceImplTest {

    @Mock
    private ExternalProjectRepository externalProjectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExternalProjectMapper externalProjectMapper;

    @InjectMocks
    private ExternalProjectServiceImpl externalProjectService;

    private ExternalProjectDto projectDto;
    private ExternalProject project;
    private User user;
    private UUID projectId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        try (AutoCloseable autoCloseable = openMocks(this)) {
            projectId = UUID.randomUUID();
            userId = UUID.randomUUID();
            projectDto = ExternalProjectDto.builder()
                    .id(projectId)
                    .userId(userId)
                    .build();
            project = mock(ExternalProject.class);
            when(project.getId()).thenReturn(projectId);
            user = mock(User.class);
            when(user.getId()).thenReturn(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createExternalProject_Success() {
        projectDto.setId(null);
        when(externalProjectMapper.toDao(any(ExternalProjectDto.class))).thenReturn(project);
        when(externalProjectRepository.save(any(ExternalProject.class))).thenReturn(Mono.just(project));
        when(externalProjectMapper.toDto(any(ExternalProject.class))).thenReturn(projectDto);

        Mono<ExternalProjectDto> result = externalProjectService.createExternalProject(projectDto);

        assertNotNull(result);
        ExternalProjectDto resultDto = result.block();
        assertNotNull(resultDto);
        assertEquals(userId, resultDto.getUserId());
        verify(externalProjectRepository, times(1)).save(any(ExternalProject.class));
        verify(externalProjectMapper, times(1)).toDto(any(ExternalProject.class));
    }

    @Test
    void getExternalProjectById_Success() {
        when(externalProjectRepository.findById(projectId)).thenReturn(Mono.just(project));
        when(externalProjectMapper.toDto(any(ExternalProject.class))).thenReturn(projectDto);

        Mono<ExternalProjectDto> result = externalProjectService.getExternalProjectById(projectId);

        assertNotNull(result);
        ExternalProjectDto resultDto = result.block();
        assertNotNull(resultDto);
        assertEquals(projectDto, resultDto);
        assertEquals(userId, resultDto.getUserId());
        verify(externalProjectRepository, times(1)).findById(projectId);
        verify(externalProjectMapper, times(1)).toDto(any(ExternalProject.class));
    }

    @Test
    void getExternalProjectById_NotFound() {
        when(externalProjectRepository.findById(projectId)).thenReturn(Mono.empty());

        Mono<ExternalProjectDto> result = externalProjectService.getExternalProjectById(projectId);
        assertTrue(result.blockOptional().isEmpty());
        verify(externalProjectRepository, times(1)).findById(projectId);
    }

    @Test
    void updateExternalProject_ProjectNotFound() {
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(externalProjectRepository.findById(projectId)).thenReturn(Mono.empty());

        assertThrows(ProjectNotFoundException.class,
                () -> externalProjectService.updateExternalProject(projectId, projectDto).block());

        verify(externalProjectRepository, times(1)).findById(projectId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateExternalProject_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Mono.empty());
        when(externalProjectRepository.findById(projectId)).thenReturn(Mono.just(project));

        assertThrows(UserNotFoundException.class,
                () -> externalProjectService.updateExternalProject(projectId, projectDto).block());

        verify(userRepository, times(1)).findById(userId);
        verify(externalProjectRepository, times(1)).findById(projectId);
    }

    @Test
    void updateExternalProject_Success() {
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(externalProjectRepository.findById(projectId)).thenReturn(Mono.just(project));
        when(externalProjectMapper.toDao(any(ExternalProjectDto.class))).thenReturn(project);
        when(externalProjectRepository.save(any(ExternalProject.class))).thenReturn(Mono.just(project));
        when(externalProjectMapper.toDto(any(ExternalProject.class))).thenReturn(projectDto);

        Mono<ExternalProjectDto> result = externalProjectService.updateExternalProject(projectId, projectDto);

        assertNotNull(result);
        ExternalProjectDto resultDto = result.block();
        assertNotNull(resultDto);
        assertEquals(projectDto, resultDto);
        verify(userRepository, times(1)).findById(userId);
        verify(externalProjectRepository, times(1)).findById(projectId);
        verify(externalProjectRepository, times(1)).save(any(ExternalProject.class));
        verify(externalProjectMapper, times(1)).toDto(any(ExternalProject.class));
    }

    @Test
    void deleteExternalProject_Success() {
        when(externalProjectRepository.deleteById(projectId)).thenReturn(Mono.empty());

        Mono<Void> result = externalProjectService.deleteExternalProject(projectId);

        assertDoesNotThrow(() -> result.block());
        verify(externalProjectRepository, times(1)).deleteById(projectId);
    }
}
