package com.krysenko4sky.auth.service.impl;

import com.krysenko4sky.auth.service.JwtUtilService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.HOURS;

@Service
public class JwtUtilServiceImpl implements JwtUtilService {

    private static final String SECRET_KEY = "secret";

    @Override
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + HOURS.toMillis(10)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    @Override
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractLogin(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
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
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}
