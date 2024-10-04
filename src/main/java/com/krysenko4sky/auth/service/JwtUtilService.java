package com.krysenko4sky.auth.service;

import io.jsonwebtoken.Claims;

import java.util.function.Function;

public interface JwtUtilService {


    String generateToken(String username);

    Boolean validateToken(String token, String username);

    Boolean isTokenExpired(String token);

    String extractLogin(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    Claims extractAllClaims(String token);
}
