package com.krysenko4sky.exception;

public class IncorrectPasswordException extends AuthenticationException {
    public IncorrectPasswordException() {
        super("Incorrect password");
    }
}
