package com.krysenko4sky.controller;

import com.krysenko4sky.model.dto.InsecureUserDto;
import com.krysenko4sky.model.dto.UserDto;
import com.krysenko4sky.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Mono<UserDto> createUser(@Valid @RequestBody InsecureUserDto user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    @Cacheable(value = "users", key = "#id")
    public Mono<UserDto> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "users", key = "#id")
    public Mono<UserDto> updateUser(@PathVariable UUID id, @Valid @RequestBody UserDto user) {
        return userService.updateUser(id, user);
    }

    @PatchMapping("/{id}")
    public Mono<UserDto> updateUserPassword(@PathVariable UUID id, @Valid @RequestBody InsecureUserDto user) {
        return userService.updateUserPassword(id, user);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = {"users", "external-projects-by-user"}, key = "#id")
    public Mono<Void> deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id);
    }

    @GetMapping
    public Flux<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
