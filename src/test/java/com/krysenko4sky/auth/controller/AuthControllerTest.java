package com.krysenko4sky.auth.controller;

import com.krysenko4sky.auth.model.dto.AuthRequestDto;
import com.krysenko4sky.auth.model.dto.RefreshTokenRequestDto;
import com.krysenko4sky.auth.model.dto.RegisterUserRequestDto;
import com.krysenko4sky.auth.service.UserDetailsService;
import com.krysenko4sky.controller.TestSecurityConfig;
import com.krysenko4sky.exception.IncorrectPasswordException;
import com.krysenko4sky.exception.InvalidTokenException;
import com.krysenko4sky.exception.RefreshTokenExpiredException;
import com.krysenko4sky.exception.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserDetailsService userDetailsService;

    private final RegisterUserRequestDto registerUserRequestDto = RegisterUserRequestDto.builder()
            .login("test@example.com")
            .password("123Yy!1234")
            .build();
    private final AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .login("testuser")
            .password("password")
            .build();
    private final RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto("validRefreshToken");

    @AfterEach
    public void cleanMocks() {
        Mockito.reset(userDetailsService);
    }

    @Test
    void testRegisterUser_Success() {
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
    void testLogin_Success() {
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
    void testRefreshToken_Success() {
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

    @ParameterizedTest
    @ValueSource(strings = {
            "password",
            "short1!",
            "NoDigit!",
            "12345678",
            "onlylowercase1!",
            "ONLYUPPERCASE1!",
    })
    void testRegisterUser_FailOnInvalidPassword(String password) {
        registerUserRequestDto.setPassword(password);

        webTestClient.post().uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerUserRequestDto)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void testRegisterUser_FailOnInvalidEmail() {
        registerUserRequestDto.setLogin("invalid-email");

        webTestClient.post().uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerUserRequestDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testLogin_FailOnIncorrectPassword() {
        when(userDetailsService.login(any(AuthRequestDto.class)))
                .thenReturn(Mono.error(new IncorrectPasswordException()));

        webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authRequestDto)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .value(response -> assertEquals("Incorrect password", response));
    }

    @Test
    void testLogin_FailOnUserNotFound() {
        when(userDetailsService.login(any(AuthRequestDto.class)))
                .thenReturn(Mono.error(new UserNotFoundException(authRequestDto.getLogin())));

        webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authRequestDto)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .value(response -> assertEquals("User with login: 'testuser' not exist.", response));
    }

    @Test
    void testRefreshToken_FailOnRefreshTokenExpired() {
        when(userDetailsService.refreshAccessToken(any(String.class)))
                .thenReturn(Mono.error(new RefreshTokenExpiredException()));

        webTestClient.post().uri("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(refreshTokenRequestDto)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .value(response -> assertEquals("Refresh token expired. Please login.", response));
    }

    @Test
    void testRefreshToken_FailOnInvalidAccessToken() {
        when(userDetailsService.refreshAccessToken(any(String.class)))
                .thenReturn(Mono.error(new InvalidTokenException()));

        webTestClient.post().uri("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(refreshTokenRequestDto)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .value(response -> assertEquals("Token is invalid!", response));
    }
}
