package ru.practicum.shareit.exception;

import lombok.Getter;

public class NotExistsException extends RuntimeException {
    @Getter
    private final String className;

    public NotExistsException(String className, String message) {
        super(message);
        this.className = className;
    }

}
