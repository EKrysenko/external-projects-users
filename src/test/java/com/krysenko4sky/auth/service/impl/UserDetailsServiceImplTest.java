package com.krysenko4sky.auth.service.impl;

import com.krysenko4sky.auth.model.dao.UserDetails;
import com.krysenko4sky.auth.model.dto.AuthRequestDto;
import com.krysenko4sky.auth.model.dto.RegisterUserRequestDto;
import com.krysenko4sky.auth.repository.UserDetailsRepository;
import com.krysenko4sky.auth.service.PasswordService;
import com.krysenko4sky.auth.service.TokenProvider;
import com.krysenko4sky.exception.IncorrectPasswordException;
import com.krysenko4sky.exception.InvalidTokenException;
import com.krysenko4sky.exception.RefreshTokenExpiredException;
import com.krysenko4sky.exception.UserNotFoundException;
import com.krysenko4sky.service.TokenValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private TokenValidator tokenValidator;

    @BeforeEach
    void setUp() {
        try (AutoCloseable autoCloseable = openMocks(this)) {
            userDetailsService = new UserDetailsServiceImpl(userDetailsRepository, passwordService, tokenProvider, tokenValidator);
            Mockito.reset(userDetailsRepository, passwordService, tokenProvider, tokenValidator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRegister_UserAlreadyExists() {
        RegisterUserRequestDto requestDto = RegisterUserRequestDto.builder()
                .login("test@example.com")
                .password("password123")
                .build();

        UserDetails existingUser = UserDetails.builder()
                .email("test@example.com")
                .build();

        when(userDetailsRepository.findByEmail(requestDto.getLogin())).thenReturn(Mono.just(existingUser));

        Mono<ResponseEntity<String>> response = userDetailsService.register(requestDto);
        ResponseEntity<String> result = response.block();

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("User with email test@example.com already exists", result.getBody());
        verify(userDetailsRepository).findByEmail(any());
        verify(userDetailsRepository, never()).save(any());
    }

    @Test
    void testRegister_Success() {
        RegisterUserRequestDto requestDto = RegisterUserRequestDto.builder()
                .login("test@example.com")
                .password("password123")
                .build();

        when(userDetailsRepository.findByEmail(requestDto.getLogin())).thenReturn(Mono.empty());
        when(passwordService.hashPassword(requestDto.getPassword())).thenReturn("hashedPassword");
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(Mono.just(UserDetails.builder().build()));

        Mono<ResponseEntity<String>> response = userDetailsService.register(requestDto);
        ResponseEntity<String> result = response.block();

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(userDetailsRepository).save(any(UserDetails.class));
    }

    @Test
    void testLogin_UserNotFound() {
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
                .login("test@example.com")
                .password("password123")
                .build();
        when(userDetailsRepository.findByEmail(authRequestDto.getLogin())).thenReturn(Mono.empty());
        assertThrows(UserNotFoundException.class, () -> userDetailsService.login(authRequestDto).block());
    }

    @Test
    void testLogin_IncorrectPassword() {
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
                .login("test@example.com")
                .password("password123")
                .build();

        UserDetails userDetails = UserDetails.builder()
                .email("test@example.com")
                .password("hashedPassword")
                .build();

        when(userDetailsRepository.findByEmail(authRequestDto.getLogin())).thenReturn(Mono.just(userDetails));
        when(passwordService.isPasswordCorrect(authRequestDto.getPassword(), userDetails.getPassword())).thenReturn(false);

        assertThrows(IncorrectPasswordException.class, () -> userDetailsService.login(authRequestDto).block());
    }

    @Test
    void testLogin_Success() {
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
                .login("test@example.com")
                .password("password123")
                .build();

        UserDetails userDetails = UserDetails.builder()
                .email("test@example.com")
                .password("hashedPassword")
                .build();

        when(userDetailsRepository.findByEmail(authRequestDto.getLogin())).thenReturn(Mono.just(userDetails));
        when(passwordService.isPasswordCorrect(authRequestDto.getPassword(), userDetails.getPassword())).thenReturn(true);
        when(tokenProvider.generateRefreshToken(userDetails.getEmail())).thenReturn("refreshToken");
        when(tokenProvider.generateToken(userDetails.getEmail())).thenReturn("accessToken");
        when(userDetailsRepository.save(userDetails)).thenReturn(Mono.just(userDetails));

        String result = userDetailsService.login(authRequestDto).block();

        assertEquals("accessToken", result);
        verify(userDetailsRepository).save(userDetails);
        assertEquals("refreshToken", userDetails.getRefreshToken());
    }

    @Test
    void testRefreshAccessToken_UserNotFound() {
        String accessToken = "someAccessToken";
        when(tokenValidator.extractLogin(accessToken)).thenReturn("test@example.com");
        when(userDetailsRepository.findByEmail("test@example.com")).thenReturn(Mono.empty());

        assertThrows(UserNotFoundException.class, () -> userDetailsService.refreshAccessToken(accessToken).block());
    }

    @Test
    void testRefreshAccessToken_InvalidToken() {
        String accessToken = "someAccessToken";
        UserDetails userDetails = UserDetails.builder()
                .email("test@example.com")
                .refreshToken("refreshToken")
                .build();

        when(tokenValidator.extractLogin(accessToken)).thenReturn("test@example.com");
        when(userDetailsRepository.findByEmail("test@example.com")).thenReturn(Mono.just(userDetails));
        when(tokenValidator.isValid(accessToken, "test@example.com")).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> userDetailsService.refreshAccessToken(accessToken).block());
    }

    @Test
    void testRefreshAccessToken_RefreshTokenExpired() {
        String accessToken = "someAccessToken";
        UserDetails userDetails = UserDetails.builder()
                .email("test@example.com")
                .refreshToken("expiredRefreshToken")
                .build();

        when(tokenValidator.extractLogin(accessToken)).thenReturn("test@example.com");
        when(userDetailsRepository.findByEmail("test@example.com")).thenReturn(Mono.just(userDetails));
        when(tokenValidator.isValid(accessToken, "test@example.com")).thenReturn(true);
        when(tokenValidator.isTokenExpired("expiredRefreshToken")).thenReturn(true);

        assertThrows(RefreshTokenExpiredException.class, () -> userDetailsService.refreshAccessToken(accessToken).block());
    }

    @Test
    void testRefreshAccessToken_Success() {
        String accessToken = "someAccessToken";
        UserDetails userDetails = UserDetails.builder()
                .email("test@example.com")
                .refreshToken("validRefreshToken")
                .build();

        when(tokenValidator.extractLogin(accessToken)).thenReturn("test@example.com");
        when(userDetailsRepository.findByEmail("test@example.com")).thenReturn(Mono.just(userDetails));
        when(tokenValidator.isValid(accessToken, "test@example.com")).thenReturn(true);
        when(tokenValidator.isTokenExpired("validRefreshToken")).thenReturn(false);
        when(tokenProvider.generateToken("test@example.com")).thenReturn("newAccessToken");

        String result = userDetailsService.refreshAccessToken(accessToken).block();

        assertEquals("newAccessToken", result);
    }
}
