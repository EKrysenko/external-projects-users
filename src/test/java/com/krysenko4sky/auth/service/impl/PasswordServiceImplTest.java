package com.krysenko4sky.auth.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordServiceImplTest {

    private PasswordServiceImpl passwordService;
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = Mockito.mock(BCryptPasswordEncoder.class);
        passwordService = new PasswordServiceImpl(encoder);
    }

    @Test
    void testHashPassword() {
        String plainPassword = "password123";
        String hashedPassword = "$2a$10$abcdefghijklmnopqrstuv";
        Mockito.when(encoder.encode(plainPassword)).thenReturn(hashedPassword);
        String result = passwordService.hashPassword(plainPassword);
        assertEquals(result, hashedPassword);
        Mockito.verify(encoder).encode(plainPassword); 
    }

    @Test
    void testIsPasswordCorrect_WithCorrectPassword() {
        String plainPassword = "password123";
        String hashedPassword = "$2a$10$abcdefghijklmnopqrstuv";
        Mockito.when(encoder.matches(plainPassword, hashedPassword)).thenReturn(true);
        boolean result = passwordService.isPasswordCorrect(plainPassword, hashedPassword);
        assertTrue(result); 
        Mockito.verify(encoder).matches(plainPassword, hashedPassword); 
    }

    @Test
    void testIsPasswordCorrect_WithIncorrectPassword() {
        String plainPassword = "wrongPassword";
        String hashedPassword = "$2a$10$abcdefghijklmnopqrstuv";
        Mockito.when(encoder.matches(plainPassword, hashedPassword)).thenReturn(false);
        boolean result = passwordService.isPasswordCorrect(plainPassword, hashedPassword);
        assertFalse(result); 
        Mockito.verify(encoder).matches(plainPassword, hashedPassword); 
    }
}
