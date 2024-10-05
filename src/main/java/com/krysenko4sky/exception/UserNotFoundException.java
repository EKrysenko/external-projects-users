package com.krysenko4sky.exception;

import java.util.UUID;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(String login) {
        super(String.format("User with login: '%s' not exist.", login));
    }

    public UserNotFoundException(UUID id) {
        super(String.format("User with id: '%s' not exist.", id));
    }
}
