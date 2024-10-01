package com.krysenko4sky.config;

import com.krysenko4sky.config.filter.SuperUserFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static com.krysenko4sky.model.Role.ADMIN;
import static com.krysenko4sky.model.Role.SUPERUSER;
import static com.krysenko4sky.model.Role.USER;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {


    @Bean
    public SuperUserFilter superUserFilter() {
        return new SuperUserFilter();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, SuperUserFilter superUserFilter) {
        http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyRole(USER.name(), ADMIN.name(), SUPERUSER.name())
                        .pathMatchers(HttpMethod.PUT, "/api/users/**").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyRole(ADMIN.name(), SUPERUSER.name())
                        .pathMatchers(HttpMethod.POST, "/api/users/{id}/grant-admin-role").hasRole(SUPERUSER.name())
                        .anyExchange().permitAll()
                )
                .addFilterAfter(superUserFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }
}
