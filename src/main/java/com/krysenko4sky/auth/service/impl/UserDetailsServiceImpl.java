package com.krysenko4sky.auth.service.impl;

import com.krysenko4sky.auth.model.dao.UserDetails;
import com.krysenko4sky.auth.model.dto.AuthRequestDto;
import com.krysenko4sky.auth.model.dto.RegisterUserRequestDto;
import com.krysenko4sky.auth.repository.UserDetailsRepository;
import com.krysenko4sky.auth.service.TokenProvider;
import com.krysenko4sky.auth.service.PasswordService;
import com.krysenko4sky.auth.service.UserDetailsService;
import com.krysenko4sky.service.TokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDetailsRepository userDetailsRepository;
    private final PasswordService passwordService;
    private final TokenProvider tokenProvider;
    private final TokenValidator tokenValidator;

    @Autowired
    public UserDetailsServiceImpl(UserDetailsRepository userDetailsRepository, PasswordService passwordService, TokenProvider tokenProvider, TokenValidator tokenValidator) {
        this.userDetailsRepository = userDetailsRepository;
        this.passwordService = passwordService;
        this.tokenProvider = tokenProvider;
        this.tokenValidator = tokenValidator;
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
        return userDetailsRepository.findByEmail(authRequestDto.getLogin())
                .switchIfEmpty(Mono.error(new RuntimeException("user not found")))
                .flatMap(
                userDetails -> {
                    boolean isPasswordCorrect = passwordService.isPasswordCorrect(authRequestDto.getPassword(), userDetails.getPassword());
                    if (isPasswordCorrect) {
                        userDetails.setRefreshToken(tokenProvider.generateRefreshToken(userDetails.getEmail()));
                        return userDetailsRepository.save(userDetails)
                                .map(savedUser -> tokenProvider.generateToken(authRequestDto.getLogin())); // Возвращаем access токен
                    } else {
                        return Mono.error(new IllegalArgumentException("wrong password"));
                    }
                }
        );
    }

    @Override
    public Mono<String> refreshAccessToken(String accessToken) {
        String login = tokenValidator.extractLogin(accessToken);
        return userDetailsRepository.findByEmail(login)
                .switchIfEmpty(Mono.error(new RuntimeException("user not found")))
                .flatMap(
                userDetails -> {
                    if (tokenValidator.isValid(accessToken, login)) {
                        if (!tokenValidator.isTokenExpired(userDetails.getRefreshToken())) {
                            return Mono.just(tokenProvider.generateToken(login));
                        } else {
                            return Mono.error(new IllegalStateException("Refresh token expired"));
                        }
                    } else {
                        return Mono.error(new IllegalArgumentException("token not valid"));
                    }
                });
    }
}