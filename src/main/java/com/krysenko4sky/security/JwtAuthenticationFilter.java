package com.krysenko4sky.security;

import com.krysenko4sky.service.TokenValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final String BEARER = "Bearer ";

    @Autowired
    private TokenValidator tokenValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER)) {
            String token = StringUtils.substringAfter(authHeader, BEARER);
            if (!tokenValidator.isValid(token)) {
                return unauthorizedResponse(exchange, "Token is invalid!");
            } else if (tokenValidator.isTokenExpired(token)) {
                return unauthorizedResponse(exchange, "Access token is expired. Refresh token.");
            } else {
                String login = tokenValidator.extractLogin(token);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(login, null, List.of());
                return chain.filter(exchange).contextWrite(withAuthentication(authenticationToken));
            }
        }
        return chain.filter(exchange);
    }


    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(message.getBytes());
        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }
}