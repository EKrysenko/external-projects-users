package com.krysenko4sky.auth.controller;

import com.krysenko4sky.auth.model.dto.AuthRequestDto;
import com.krysenko4sky.auth.model.dto.RefreshTokenRequestDto;
import com.krysenko4sky.auth.model.dto.RegisterUserRequestDto;
import com.krysenko4sky.auth.service.UserDetailsService;
import com.krysenko4sky.controller.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserDetailsService userDetailsService;

    private final RegisterUserRequestDto registerUserRequestDto = RegisterUserRequestDto.builder().login("test@example.com").password("123Yy!1234").build();
    private final AuthRequestDto authRequestDto = AuthRequestDto.builder().login("testuser").password("password").build();
    private final RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJldmdlbml5LmtyeXNlbmtvQHlhbmRleC5ydSIsImlhdCI6MTcyODE0MjEwMiwiZXhwIjoxNzI4MTQyNzAyfQ.rM7TsOJmf_B6IOMnIcUMedIpYwy8d3VPNMz2jo5t-e8");

    @Test
    void testRegisterUser() {
        Mockito.when(userDetailsService.register(any(RegisterUserRequestDto.class)))
                .thenReturn(Mono.just(ResponseEntity.ok("User registered successfully")));

        webTestClient.post().uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerUserRequestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> {
                    assert response != null;
                    assert response.equals("User registered successfully");
                });

        Mockito.verify(userDetailsService).register(any(RegisterUserRequestDto.class));
    }

    @Test
    void testLogin() {
        Mockito.when(userDetailsService.login(any(AuthRequestDto.class)))
                .thenReturn(Mono.just("mockJwtToken"));

        webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authRequestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(token -> {
                    assert token != null;
                    assert token.equals("mockJwtToken");
                });

        Mockito.verify(userDetailsService).login(any(AuthRequestDto.class));
    }

    @Test
    void testRefreshToken() {
        Mockito.when(userDetailsService.refreshAccessToken(any(String.class)))
                .thenReturn(Mono.just("newMockJwtToken"));

        webTestClient.post().uri("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(refreshTokenRequestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(token -> {
                    assert token != null;
                    assert token.equals("newMockJwtToken");
                });

        Mockito.verify(userDetailsService).refreshAccessToken(any(String.class));
    }
}
