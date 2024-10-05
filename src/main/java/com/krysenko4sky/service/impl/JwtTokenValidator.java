package com.krysenko4sky.service.impl;

import com.krysenko4sky.service.TokenValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtTokenValidator implements TokenValidator {

    private static final String SECRET_KEY = "secret";

    @Override
    public Boolean isValid(String token, String username) {
        final String extractedUsername = extractLogin(token);
        return (extractedUsername.equals(username));
    }

    @Override
    public Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    @Override
    public String extractLogin(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            Object rolesObject = claims.get("roles");
            if (rolesObject instanceof List<?> rolesList) {
                if (rolesList.stream().allMatch(item -> item instanceof String)) {
                    return rolesList.stream()
                            .map(item -> (String) item)
                            .collect(Collectors.toList());
                }
            }
            return Collections.emptyList();
        });
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}
