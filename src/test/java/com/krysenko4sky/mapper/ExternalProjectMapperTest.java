package com.krysenko4sky.mapper;

import com.krysenko4sky.model.dao.ExternalProject;
import com.krysenko4sky.model.dto.ExternalProjectDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExternalProjectMapperTest {

    private ExternalProjectMapper externalProjectMapper;

    private ExternalProjectDto projectDto;
    private ExternalProject projectDao;

    @BeforeEach
    void setUp() {
        externalProjectMapper = Mappers.getMapper(ExternalProjectMapper.class);

        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        projectDto = ExternalProjectDto.builder()
                .id(projectId)
                .userId(userId)
                .name("Test Project")
                .build();

        projectDao = ExternalProject.builder()
                .id(projectId)
                .userId(userId)
                .name("Test Project")
                .build();
    }

    @Test
    void toDao_Success() {
        ExternalProject result = externalProjectMapper.toDao(projectDto);

        assertNotNull(result);
        assertEquals(projectDto.getId(), result.getId());
        assertEquals(projectDto.getUserId(), result.getUserId());
        assertEquals(projectDto.getName(), result.getName());
    }

    @Test
    void toDto_Success() {
        ExternalProjectDto result = externalProjectMapper.toDto(projectDao);

        assertNotNull(result);
        assertEquals(projectDao.getId(), result.getId());
        assertEquals(projectDao.getUserId(), result.getUserId());
        assertEquals(projectDao.getName(), result.getName());
    }

    @Test
    void toDao_NullInput() {
        ExternalProject result = externalProjectMapper.toDao(null);

        assertNull(result);
    }

    @Test
    void toDto_NullInput() {
        ExternalProjectDto result = externalProjectMapper.toDto(null);

        assertNull(result);
    }
}
