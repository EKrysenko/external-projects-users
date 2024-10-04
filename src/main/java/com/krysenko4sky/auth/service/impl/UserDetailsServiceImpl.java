package com.krysenko4sky.auth.service.impl;

import com.krysenko4sky.auth.model.dao.UserDetails;
import com.krysenko4sky.auth.model.dto.AuthRequestDto;
import com.krysenko4sky.auth.model.dto.RegisterUserRequestDto;
import com.krysenko4sky.auth.repository.UserDetailsRepository;
import com.krysenko4sky.auth.service.JwtUtilService;
import com.krysenko4sky.auth.service.PasswordService;
import com.krysenko4sky.auth.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDetailsRepository userDetailsRepository;
    private final PasswordService passwordService;
    private final JwtUtilService jwtUtilService;

    @Autowired
    public UserDetailsServiceImpl(UserDetailsRepository userDetailsRepository, PasswordService passwordService, JwtUtilService jwtUtilService) {
        this.userDetailsRepository = userDetailsRepository;
        this.passwordService = passwordService;
        this.jwtUtilService = jwtUtilService;
    }

    @Override
    public Mono<ResponseEntity<String>> register(RegisterUserRequestDto dto) {
        String login = dto.getLogin();
        return userDetailsRepository.findByEmail(login)
                .flatMap(existingUser -> {
                    String message = "User with email " + login + " already exists";
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(message));
                })
                .switchIfEmpty(
                        userDetailsRepository.save(UserDetails.builder()
                                        .email(login)
                                        .password(passwordService.hashPassword(dto.getPassword()))
                                        .build())
                                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()))
                );
    }

    @Override
    public Mono<String> login(AuthRequestDto authRequestDto) {
        return userDetailsRepository.findByEmail(authRequestDto.getLogin()).flatMap(
                userDetails -> {
                    boolean isPasswordCorrect = passwordService.isPasswordCorrect(authRequestDto.getPassword(), userDetails.getPassword());
                    if (isPasswordCorrect) {
                        return Mono.just(jwtUtilService.generateToken(authRequestDto.getLogin()));
                    } else {
                        return Mono.error(new IllegalArgumentException("wrong password"));
                    }
                }
        );
    }

    @Override
    public Mono<UserDetails> getUserDetailsByLogin(String login) {
        return userDetailsRepository.findByEmail(login);
    }
}
