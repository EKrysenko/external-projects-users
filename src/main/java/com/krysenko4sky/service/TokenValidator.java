package com.krysenko4sky.service;

import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.function.Function;

public interface TokenValidator {

    Boolean isValid(String token, String username);

    Boolean isTokenExpired(String token);

    String extractLogin(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    Claims extractAllClaims(String token);

    List<String> extractRoles(String jwt);
}
