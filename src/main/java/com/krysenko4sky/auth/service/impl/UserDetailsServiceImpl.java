package com.krysenko4sky.auth.service.impl;

import com.krysenko4sky.auth.model.dao.UserDetails;
import com.krysenko4sky.auth.model.dto.AuthRequestDto;
import com.krysenko4sky.auth.model.dto.RegisterUserRequestDto;
import com.krysenko4sky.auth.repository.UserDetailsRepository;
import com.krysenko4sky.auth.service.PasswordService;
import com.krysenko4sky.auth.service.TokenProvider;
import com.krysenko4sky.auth.service.UserDetailsService;
import com.krysenko4sky.exception.IncorrectPasswordException;
import com.krysenko4sky.exception.InvalidTokenException;
import com.krysenko4sky.exception.RefreshTokenExpiredException;
import com.krysenko4sky.exception.UserNotFoundException;
import com.krysenko4sky.service.TokenValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
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
                    log.error("User exists");
                    String message = "User with email " + login + " already exists";
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(message));
                })
                .switchIfEmpty(Mono.defer(() ->
                        userDetailsRepository.save(UserDetails.builder()
                                        .email(login)
                                        .password(passwordService.hashPassword(dto.getPassword()))
                                        .build())
                                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build())))
                );
    }

    @Override
    public Mono<String> login(AuthRequestDto authRequestDto) {
        String login = authRequestDto.getLogin();
        return userDetailsRepository.findByEmail(login)
                .switchIfEmpty(Mono.error(new UserNotFoundException(login)))
                .flatMap(
                        userDetails -> {
                            boolean isPasswordCorrect = passwordService.isPasswordCorrect(authRequestDto.getPassword(), userDetails.getPassword());
                            if (isPasswordCorrect) {
                                userDetails.setRefreshToken(tokenProvider.generateRefreshToken(userDetails.getEmail()));
                                return userDetailsRepository.save(userDetails)
                                        .map(savedUser -> tokenProvider.generateToken(login));
                            } else {
                                log.debug("Incorrect password for user: {}", login);
                                return Mono.error(new IncorrectPasswordException());
                            }
                        }
                );
    }

    @Override
    public Mono<String> refreshAccessToken(String accessToken) {
        String login = tokenValidator.extractLogin(accessToken);
        return userDetailsRepository.findByEmail(login)
                .switchIfEmpty(Mono.error(new UserNotFoundException(login)))
                .flatMap(
                        userDetails -> {
                            if (tokenValidator.isValid(accessToken, login)) {
                                if (!tokenValidator.isTokenExpired(userDetails.getRefreshToken())) {
                                    return Mono.just(tokenProvider.generateToken(login));
                                } else {
                                    log.debug("Refresh token expired for user: {}", login);
                                    return Mono.error(new RefreshTokenExpiredException());
                                }
                            } else {
                                log.error("Invalid token for user: {}", login);
                                return Mono.error(new InvalidTokenException());
                            }
                        });
    }
}