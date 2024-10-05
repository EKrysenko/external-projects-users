package com.krysenko4sky.auth.service;

import com.krysenko4sky.auth.model.dto.AuthRequestDto;
import com.krysenko4sky.auth.model.dto.RegisterUserRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface UserDetailsService {
    Mono<ResponseEntity<String>> register(@Valid RegisterUserRequestDto user);

    Mono<String> login(AuthRequestDto authRequestDto);

    Mono<String> refreshAccessToken(String accessToken);
}
