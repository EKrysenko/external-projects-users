package com.krysenko4sky.service.impl;

import com.google.common.base.Preconditions;
import com.krysenko4sky.exception.UserNotFoundException;
import com.krysenko4sky.logging.LogArguments;
import com.krysenko4sky.mapper.UserMapper;
import com.krysenko4sky.model.dao.User;
import com.krysenko4sky.model.dto.UserDto;
import com.krysenko4sky.repository.ExternalProjectRepository;
import com.krysenko4sky.repository.UserRepository;
import com.krysenko4sky.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@LogArguments
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ExternalProjectRepository externalProjectRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ExternalProjectRepository externalProjectRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.externalProjectRepository = externalProjectRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Mono<UserDto> createUser(UserDto dto) {
        Preconditions.checkArgument(dto.getId() == null, "Field 'id' must be empty");
        User dao = userMapper.toDao(dto);
        return userRepository.save(dao).map(userMapper::toDto);
    }

    @Override
    public Mono<UserDto> getUserById(UUID id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Override
    public Mono<UserDto> updateUser(UUID id, UserDto dto) {
        Preconditions.checkArgument(dto.getId().equals(id), "id in path and in dto must be the same");
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .flatMap(existingUser -> {
                    existingUser.setUsername(dto.getUsername());
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
