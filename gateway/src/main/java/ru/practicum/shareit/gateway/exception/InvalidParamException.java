package ru.practicum.shareit.gateway.exception;

import lombok.Getter;

public class InvalidParamException extends RuntimeException {
    @Getter
    private final String paramName;

    public InvalidParamException(String paramName, String message) {
        super(message);
        this.paramName = paramName;
    }
}
