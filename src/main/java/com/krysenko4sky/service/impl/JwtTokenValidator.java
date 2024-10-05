package com.krysenko4sky.service.impl;

import com.krysenko4sky.exception.InvalidTokenException;
import com.krysenko4sky.service.TokenValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtTokenValidator implements TokenValidator {

    @Value("${secret}")
    private String jwtSigningSecret;

    @Override
    public Boolean isValid(String token, String username) {
        try {
            final String extractedUsername = extractLogin(token);
            return extractedUsername.equals(username);
        } catch (InvalidTokenException e) {
            return false;
        }
    }

    @Override
    public Boolean isValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (InvalidTokenException e) {
            return false;
        }
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
        try {
            return (Claims) Jwts.parser().verifyWith(getSecretKey()).build().parse(token).getPayload();
        }catch (ExpiredJwtException e) {
            log.debug("Token expired.");
            return e.getClaims();
        } catch (SignatureException e) {
            log.error("Parse JWT token error: {}", e.getMessage());
            throw new InvalidTokenException(e.getMessage());
        } catch (Exception e) {
            log.error("Error on parse JWT token: {}", e.getMessage(), e);
            throw new InvalidTokenException(e.getMessage());
        }
    }

    public SecretKey getSecretKey() {
        byte[] keyBytes = jwtSigningSecret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
    }
}
