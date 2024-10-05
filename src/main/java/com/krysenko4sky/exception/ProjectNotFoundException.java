package com.krysenko4sky.exception;

import java.util.UUID;

public class ProjectNotFoundException extends EntityNotFoundException {

    public ProjectNotFoundException(UUID id) {
        super(String.format("Project with id: '%s' not exist.", id));
    }
}
