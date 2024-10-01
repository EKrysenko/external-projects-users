package com.krysenko4sky.service.impl;

import com.krysenko4sky.model.User;
import com.krysenko4sky.repository.UserRepository;
import com.krysenko4sky.service.PasswordService;
import com.krysenko4sky.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    @Override
    public Mono<Boolean> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> passwordService.checkPassword(password, user.getPassword()))
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<User> createUser(User user) {
        String hashedPassword = passwordService.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    @Override
    public Mono<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<User> updateUser(UUID id, User user) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setEmail(user.getEmail());
                    existingUser.setPassword(user.getPassword());
                    existingUser.setUsername(user.getUsername());
                    return userRepository.save(existingUser);
                });
    }

    @Override
    public Mono<Void> deleteUser(UUID id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }
}
