package com.krysenko4sky.security;

import com.krysenko4sky.auth.service.JwtUtilService;
import com.krysenko4sky.auth.service.UserDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Autowired
    private JwtUtilService jwtUtilService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = StringUtils.substringAfter(authHeader, "Bearer ");
            String login = jwtUtilService.extractLogin(jwt);

            if (login != null) {
                return userDetailsService.getUserDetailsByLogin(login)
                        .switchIfEmpty(Mono.error(new RuntimeException("user not found")))
                        .flatMap(userDetails -> {
                            if (jwtUtilService.validateToken(jwt, userDetails.getEmail())) {
                                UsernamePasswordAuthenticationToken authenticationToken =
                                        new UsernamePasswordAuthenticationToken(userDetails, null, null);

                                return chain.filter(exchange).contextWrite(withAuthentication(authenticationToken));
                            } else {
                                return chain.filter(exchange);
                            }
                        });
            }
        }
        return chain.filter(exchange);
    }
}
