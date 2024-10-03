package com.krysenko4sky.model.mapper;

import com.krysenko4sky.model.dao.User;
import com.krysenko4sky.model.dto.UserDto;
import com.krysenko4sky.model.dto.InsecureUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toDao(InsecureUserDto dto);

    UserDto toDto(User dao);

}