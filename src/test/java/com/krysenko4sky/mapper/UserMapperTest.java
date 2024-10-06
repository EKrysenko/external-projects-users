package com.krysenko4sky.mapper;

import com.krysenko4sky.model.dao.User;
import com.krysenko4sky.model.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private UserMapper userMapper;

    private UserDto userDto;
    private User userDao;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        UUID userId = UUID.randomUUID();
        userDto = UserDto.builder()
                .id(userId)
                .username("test_user")
                .build();

        userDao = new User();
        userDao.setId(userId);
        userDao.setUsername("test_user");
    }

    @Test
    void toDao_Success() {
        User result = userMapper.toDao(userDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getUsername(), result.getUsername());
    }

    @Test
    void toDto_Success() {
        UserDto result = userMapper.toDto(userDao);

        assertNotNull(result);
        assertEquals(userDao.getId(), result.getId());
        assertEquals(userDao.getUsername(), result.getUsername());
    }

    @Test
    void toDao_NullInput() {
        User result = userMapper.toDao(null);

        assertNull(result);
    }

    @Test
    void toDto_NullInput() {
        UserDto result = userMapper.toDto(null);

        assertNull(result);
    }
}
