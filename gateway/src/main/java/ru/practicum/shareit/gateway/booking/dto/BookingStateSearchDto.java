package ru.practicum.shareit.gateway.booking.dto;

import java.util.Optional;

public enum BookingStateSearchDto {
    // Все
    ALL,
    // Текущие
    CURRENT,
    // Будущие
    FUTURE,
    // Завершенные
    PAST,
    // Отклоненные
    REJECTED,
    // Ожидающие подтверждения
    WAITING;

    public static Optional<BookingStateSearchDto> from(String stringState) {
        for (BookingStateSearchDto state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
