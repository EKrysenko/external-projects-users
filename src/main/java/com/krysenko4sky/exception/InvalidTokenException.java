package com.krysenko4sky.exception;

public class InvalidTokenException extends AuthenticationException{
    public InvalidTokenException() {
        super("Token is invalid!");
    }

    public InvalidTokenException(String error) {
        super("Token is invalid: " + error);
    }
}
