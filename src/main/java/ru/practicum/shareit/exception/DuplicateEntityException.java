package ru.practicum.shareit.exception;

import lombok.Getter;

public class DuplicateEntityException extends RuntimeException {
    @Getter
    private final String entityName;

    public DuplicateEntityException(String entityName, String message) {
        super(message);
        this.entityName = entityName;
    }
}
