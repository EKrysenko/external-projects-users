package com.krysenko4sky.controller;

import com.krysenko4sky.model.User;
import com.krysenko4sky.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Mono<User> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public Mono<User> updateUser(@PathVariable UUID id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id);
    }


    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
