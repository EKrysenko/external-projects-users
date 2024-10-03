package com.krysenko4sky.service.impl;

import com.google.common.base.Preconditions;
import com.krysenko4sky.model.dao.User;
import com.krysenko4sky.model.dto.InsecureUserDto;
import com.krysenko4sky.model.dto.UserDto;
import com.krysenko4sky.model.mapper.UserMapper;
import com.krysenko4sky.repository.ExternalProjectRepository;
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
    private final ExternalProjectRepository externalProjectRepository;
    private final PasswordService passwordService;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ExternalProjectRepository externalProjectRepository, PasswordService passwordService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.externalProjectRepository = externalProjectRepository;
        this.passwordService = passwordService;
        this.userMapper = userMapper;
    }

    @Override
    public Mono<Boolean> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new RuntimeException("user not found")))
                .map(user -> passwordService.checkPassword(password, user.getPassword()))
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<UserDto> createUser(InsecureUserDto dto) {
        Preconditions.checkArgument(dto.getId() == null, "Field 'id' must be empty");
        User dao = userMapper.toDao(dto);
        dao.setPassword(passwordService.hashPassword(dto.getPassword()));
        return userRepository.save(dao).map(userMapper::toDto);
    }

    @Override
    public Mono<UserDto> getUserById(UUID id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Override
    public Mono<UserDto> updateUser(UUID id, UserDto dto) {
        Preconditions.checkArgument(dto.getId() == id, "id in path and in dto must be the same");
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("user not found")))
                .flatMap(existingUser -> {
                    existingUser.setEmail(dto.getEmail());
                    existingUser.setUsername(dto.getUsername());
                    return userRepository.save(existingUser)
                            .map(userMapper::toDto);
                });
    }

    @Override
    public Mono<UserDto> updateUserPassword(UUID id, InsecureUserDto dto) {
        Preconditions.checkArgument(dto.getId() == id, "id in path and in dto must be the same");
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("user not found")))
                .flatMap(existingUser -> {
                    if (passwordService.checkPassword(dto.getPassword(), existingUser.getPassword())) {
                        throw new IllegalArgumentException("New password cannot be same as old");
                    }
                    existingUser.setPassword(passwordService.hashPassword(dto.getPassword()));
                    return userRepository.save(existingUser)
                            .map(userMapper::toDto);
                });
    }

    @Override
    public Mono<Void> deleteUser(UUID id) {
        return externalProjectRepository.findByUserId(id)
                .flatMap(project -> externalProjectRepository.deleteById(project.getId()))
                .then(userRepository.deleteById(id));
    }

    @Override
    public Flux<UserDto> getAllUsers() {
        return userRepository.findAll().map(userMapper::toDto);
    }
}
