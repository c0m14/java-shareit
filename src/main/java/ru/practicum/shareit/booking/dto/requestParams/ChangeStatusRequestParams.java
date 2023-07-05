package ru.practicum.shareit.booking.dto.requestParams;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChangeStatusRequestParams {
    Long userId;
    Long bookingId;
    boolean approved;
}
