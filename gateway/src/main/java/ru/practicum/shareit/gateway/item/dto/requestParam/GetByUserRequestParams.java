package ru.practicum.shareit.gateway.item.dto.requestParam;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetByUserRequestParams {
    Long userId;
    int from;
    int size;
}
