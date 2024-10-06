package com.krysenko4sky.exception;

public class RefreshTokenExpiredException extends AuthenticationException {
    public RefreshTokenExpiredException() {
        super("Refresh token expired. Please login.");
    }
}
