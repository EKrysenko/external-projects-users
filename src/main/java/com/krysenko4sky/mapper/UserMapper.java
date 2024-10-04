package com.krysenko4sky.mapper;

import com.krysenko4sky.model.dao.User;
import com.krysenko4sky.model.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDao(UserDto dto);

    UserDto toDto(User dao);

}