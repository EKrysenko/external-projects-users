package com.krysenko4sky.service.impl;

import com.krysenko4sky.exception.UserNotFoundException;
import com.krysenko4sky.mapper.UserMapper;
import com.krysenko4sky.model.dao.User;
import com.krysenko4sky.model.dto.UserDto;
import com.krysenko4sky.repository.ExternalProjectRepository;
import com.krysenko4sky.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExternalProjectRepository externalProjectRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        try (AutoCloseable ignored = openMocks(this)) {
            userId = UUID.randomUUID();
            userDto = UserDto.builder()
                    .id(userId)
                    .username("test_user")
                    .build();

            user = new User();
            user.setId(userId);
            user.setUsername("test_user");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createUser_Success() {
        userDto.setId(null);
        when(userMapper.toDao(any(UserDto.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        Mono<UserDto> result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(userDto, result.block());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toDao(any(UserDto.class));
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        Mono<UserDto> result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userDto, result.block());
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        Mono<UserDto> result = userService.getUserById(userId);

        assertTrue(result.blockOptional().isEmpty());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        Mono<UserDto> result = userService.updateUser(userId, userDto);

        assertNotNull(result);
        assertEquals(userDto, result.block());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    void updateUser_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, userDto).block());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_FailedOnIdMismatch() {
        UUID pathId = UUID.randomUUID();
        UUID dtoId = UUID.randomUUID();
        userDto.setId(dtoId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(pathId, userDto).block());

        assertEquals("id in path and in dto must be the same", exception.getMessage());
    }

    @Test
    void deleteUser_Success() {
        when(externalProjectRepository.findByUserId(userId)).thenReturn(Flux.empty());
        when(userRepository.deleteById(userId)).thenReturn(Mono.empty());

        Mono<Void> result = userService.deleteUser(userId);

        assertDoesNotThrow(() -> result.block());
        verify(externalProjectRepository, times(1)).findByUserId(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Flux.just(user));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        Flux<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(userDto, result.blockFirst());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDto(any(User.class));
    }
}
