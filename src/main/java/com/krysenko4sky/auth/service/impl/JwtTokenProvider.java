package com.krysenko4sky.auth.service.impl;

import com.krysenko4sky.auth.service.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class JwtTokenProvider implements TokenProvider {

    private static final String SECRET_KEY = "secret";

    @Override
    public String generateToken(String login) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + MINUTES.toMillis(10)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    @Override
    public String generateRefreshToken(String login) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + HOURS.toMillis(24)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
