package com.krysenko4sky.service;

import com.krysenko4sky.model.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {

    Mono<UserDto> createUser(UserDto user);

    Mono<UserDto> getUserById(UUID id);

    Mono<UserDto> updateUser(UUID id, UserDto user);

    Mono<Void> deleteUser(UUID id);

    Flux<UserDto> getAllUsers();
}
