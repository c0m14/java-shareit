package ru.practicum.shareit.booking.dto.requestParams;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetByStaterequestParams {
    Long userId;
    String state;
    int from;
    int size;
}
