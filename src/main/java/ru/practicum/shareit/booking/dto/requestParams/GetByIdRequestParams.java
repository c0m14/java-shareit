package ru.practicum.shareit.booking.dto.requestParams;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetByIdRequestParams {
    Long userId;
    Long bookingId;
}
