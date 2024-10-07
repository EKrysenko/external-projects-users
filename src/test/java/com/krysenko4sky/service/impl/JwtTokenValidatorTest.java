package com.krysenko4sky.service.impl;

import com.krysenko4sky.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

class JwtTokenValidatorTest {

    private JwtTokenValidator jwtTokenValidator;

    @BeforeEach
    void setUp() {
        try (AutoCloseable ignored = openMocks(this)) {
            jwtTokenValidator = new JwtTokenValidator();
            String secret = "z1lun9qbiBW3Hy6bVVdjpa3iK23ZjjApOMaXIVb0OZw=";
            ReflectionTestUtils.setField(jwtTokenValidator, "jwtSigningSecret", secret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void assertIsValid_WithValidTokenAndMatchingUsername() {
        String token = Jwts.builder()
                .subject("testUser")
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(jwtTokenValidator.getSecretKey())
                .compact();

        assertTrue(jwtTokenValidator.isValid(token, "testUser"));
    }

    @Test
    void assertNotValid_WithDifferentUsername() {
        String token = Jwts.builder().subject("testUser")
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(jwtTokenValidator.getSecretKey())
                .compact();

        assertFalse(jwtTokenValidator.isValid(token, "anotherUser"));
    }

    @Test
    void assertIsValid_EvenIfExpired() {
        String token = Jwts.builder()
                .subject("testUser")
                .expiration(new Date(System.currentTimeMillis() - 10000))
                .signWith(jwtTokenValidator.getSecretKey())
                .compact();

        assertTrue(jwtTokenValidator.isValid(token, "testUser"));
        assertTrue(jwtTokenValidator.isTokenExpired(token));
    }

    @Test
    void assertNotValid() {
        String invalidToken = "invalidToken";

        assertFalse(jwtTokenValidator.isValid(invalidToken));
    }

    @Test
    void assertExpired() {
        String token = Jwts.builder().subject("testUser")
                .expiration(new Date(System.currentTimeMillis() - 10000))
                .signWith(jwtTokenValidator.getSecretKey())
                .compact();

        assertTrue(jwtTokenValidator.isTokenExpired(token));
    }

    @Test
    void canExtractLogin_WithValidToken() {
        String token = Jwts.builder().subject("testUser").expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(jwtTokenValidator.getSecretKey())
                .compact();

        String username = jwtTokenValidator.extractLogin(token);
        assertEquals("testUser", username);
    }

    @Test
    void canExtractRoles_WithValidToken() {
        String token = Jwts.builder().subject("testUser")
                .claim("roles", Arrays.asList("ROLE_USER", "ROLE_ADMIN")).expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(jwtTokenValidator.getSecretKey())
                .compact();

        List<String> roles = jwtTokenValidator.extractRoles(token);
        assertEquals(Arrays.asList("ROLE_USER", "ROLE_ADMIN"), roles);
    }

    @Test
    void canExtractRoles_WithNoRoles() {
        String token = Jwts.builder().subject("testUser").expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(jwtTokenValidator.getSecretKey())
                .compact();

        List<String> roles = jwtTokenValidator.extractRoles(token);
        assertEquals(Collections.emptyList(), roles);
    }

    @Test
    void canExtractAllClaims_WithValidToken() {
        String token = Jwts.builder().subject("testUser").expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(jwtTokenValidator.getSecretKey())
                .compact();

        Claims claims = jwtTokenValidator.extractAllClaims(token);
        assertEquals("testUser", claims.getSubject());
    }

    @Test
    void canExtractAllClaims_WithExpiredToken() {
        String token = Jwts.builder()
                .subject("testUser")
                .expiration(new Date(System.currentTimeMillis() - 10000))
                .signWith(jwtTokenValidator.getSecretKey())
                .compact();

        assertDoesNotThrow(() -> {
            jwtTokenValidator.extractAllClaims(token);
        });
    }

    @Test
    void canThrowOnExtractAllClaims_WithInvalidToken() {
        String invalidToken = "invalidToken";

        assertThrows(InvalidTokenException.class, () -> jwtTokenValidator.extractAllClaims(invalidToken));
    }

    @Test
    void canThrowOnExtractAllClaims_WithSignatureException() {
        String tokenWithInvalidSignature = Jwts.builder()
                .subject("testUser")
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(createFakeSignature())
                .compact();

        assertThrows(InvalidTokenException.class, () -> jwtTokenValidator.extractAllClaims(tokenWithInvalidSignature));
    }

    private Key createFakeSignature() {
        byte[] keyBytes = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
    }
}
