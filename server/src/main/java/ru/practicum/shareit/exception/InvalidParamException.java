package ru.practicum.shareit.exception;

import lombok.Getter;

public class InvalidParamException extends RuntimeException {
    @Getter
    private final String paramName;

    public InvalidParamException(String paramName, String message) {
        super(message);
        this.paramName = paramName;
    }
}
