package com.krysenko4sky.service;

import com.krysenko4sky.model.dto.UserDto;
import com.krysenko4sky.model.dto.InsecureUserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {

    Mono<Boolean> authenticate(String email, String password);

    Mono<UserDto> createUser(InsecureUserDto user);

    Mono<UserDto> getUserById(UUID id);

    Mono<UserDto> updateUser(UUID id, UserDto user);

    Mono<UserDto> updateUserPassword(UUID id, InsecureUserDto user);

    Mono<Void> deleteUser(UUID id);

    Flux<UserDto> getAllUsers();
}
