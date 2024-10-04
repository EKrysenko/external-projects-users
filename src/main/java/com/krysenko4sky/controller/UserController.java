package com.krysenko4sky.controller;

import com.krysenko4sky.model.dto.UserDto;
import com.krysenko4sky.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users")
public class UserController {

    private static final String USERS_CACHE = "users";
    private static final String ID_KEY = "#id";
    private static final String ALL_USERS_KEY = "'allUsers'";
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @CacheEvict(value = USERS_CACHE, key = ALL_USERS_KEY)
    public Mono<UserDto> createUser(@Valid @RequestBody UserDto user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    @Cacheable(value = USERS_CACHE, key = ID_KEY)
    public Mono<UserDto> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    @Caching(evict = {
            @CacheEvict(value = USERS_CACHE, key = ID_KEY),
            @CacheEvict(value = USERS_CACHE, key = ALL_USERS_KEY)
    })
    public Mono<UserDto> updateUser(@PathVariable UUID id, @Valid @RequestBody UserDto user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    @Caching(evict = {
            @CacheEvict(value = USERS_CACHE, key = ID_KEY),
            @CacheEvict(value = USERS_CACHE, key = ALL_USERS_KEY)
    })
    public Mono<Void> deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id);
    }

    @GetMapping
    @Cacheable(value = USERS_CACHE, key = ALL_USERS_KEY)
    public Flux<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
