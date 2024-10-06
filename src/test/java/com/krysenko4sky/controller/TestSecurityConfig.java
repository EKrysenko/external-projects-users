package com.krysenko4sky.controller;

import com.krysenko4sky.auth.service.TokenProvider;
import com.krysenko4sky.service.TokenValidator;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public TokenValidator mockTokenValidator() {
        return Mockito.mock(TokenValidator.class);
    }

    @Bean
    public TokenProvider mockTokenProvider() {
        return Mockito.mock(TokenProvider.class);
    }

    @Bean
    public SecurityWebFilterChain disabledSpringSecurity(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
