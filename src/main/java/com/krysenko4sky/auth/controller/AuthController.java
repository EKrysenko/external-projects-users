package com.krysenko4sky.auth.controller;

import com.krysenko4sky.auth.model.dto.AuthRequestDto;
import com.krysenko4sky.auth.model.dto.RegisterUserRequestDto;
import com.krysenko4sky.auth.service.UserDetailsService;
import jakarta.validation.Valid;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping("/auth")
public class AuthController {

    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerUser(@Valid @RequestBody RegisterUserRequestDto user) {
        return userDetailsService.register(user);
    }

    @PostMapping("/login")
    public Mono<String> login(@RequestBody AuthRequestDto authRequestDto) {
        return userDetailsService.login(authRequestDto);
    }

    @PostMapping("/refresh")
    public Mono<String> refresh(@RequestBody String accessToken) {
        return userDetailsService.refreshAccessToken(accessToken);
    }

}