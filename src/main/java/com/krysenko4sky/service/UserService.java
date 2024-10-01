package com.krysenko4sky.service;

import com.krysenko4sky.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {

    Mono<Boolean> authenticate(String email, String password);

    Mono<User> createUser(User user);

    Mono<User> getUserById(UUID id);

    Mono<User> grantAdminRole(UUID userId);

    Mono<User> updateUser(UUID id, User user);

    Mono<Void> deleteUser(UUID id);

    Flux<User> getAllUsers();
}
