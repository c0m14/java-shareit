package ru.practicum.shareit.item.dto.requestParam;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetByUserRequestParams {
    Long userId;
    int from;
    int size;
}
