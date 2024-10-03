package com.krysenko4sky.model.mapper;

import com.krysenko4sky.model.dao.ExternalProject;
import com.krysenko4sky.model.dto.ExternalProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExternalProjectMapper {

    ExternalProject toDao(ExternalProjectDto dto);

    ExternalProjectDto toDto(ExternalProject dao);

}