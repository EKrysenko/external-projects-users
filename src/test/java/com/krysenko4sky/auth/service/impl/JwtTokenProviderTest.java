package com.krysenko4sky.auth.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.MockitoAnnotations.openMocks;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        try (AutoCloseable autoCloseable = openMocks(this)) {
            jwtTokenProvider = new JwtTokenProvider();
            String secret = "z1lun9qbiBW3Hy6bVVdjpa3iK23ZjjApOMaXIVb0OZw=";
            ReflectionTestUtils.setField(jwtTokenProvider, "jwtSigningSecret", secret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void canGenerateToken() {
        String login = "testUser";
        String token = jwtTokenProvider.generateToken(login);

        assertNotNull(token);
        Claims claims = (Claims) Jwts.parser().verifyWith(jwtTokenProvider.getSecretKey()).build().parse(token).getPayload();
        assertEquals(login, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void canGenerateRefreshToken() {
        String login = "testUser";
        String refreshToken = jwtTokenProvider.generateRefreshToken(login);

        assertNotNull(refreshToken);
        Claims claims = (Claims) Jwts.parser().verifyWith(jwtTokenProvider.getSecretKey()).build().parse(refreshToken).getPayload();
        assertEquals(login, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generatedTokenIsNotExpiredImmediately() {
        String login = "testUser";
        String token = jwtTokenProvider.generateToken(login);
        Claims claims = (Claims) Jwts.parser().verifyWith(jwtTokenProvider.getSecretKey()).build().parse(token).getPayload();
        Date expiration = claims.getExpiration();

        assertEquals(9, (expiration.getTime() - System.currentTimeMillis()) / (1000 * 60));
    }

    @Test
    void generatedRefreshTokenIsValid() {
        String login = "testUser";
        String refreshToken = jwtTokenProvider.generateRefreshToken(login);
        Claims claims = (Claims) Jwts.parser().verifyWith(jwtTokenProvider.getSecretKey()).build().parse(refreshToken).getPayload();
        Date expiration = claims.getExpiration();

        assertEquals(23, (expiration.getTime() - System.currentTimeMillis()) / (1000 * 60 * 60));
    }
}
