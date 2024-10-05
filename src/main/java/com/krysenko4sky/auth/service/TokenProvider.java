package com.krysenko4sky.auth.service;

public interface TokenProvider {

    String generateToken(String login);

    String generateRefreshToken(String login);
}