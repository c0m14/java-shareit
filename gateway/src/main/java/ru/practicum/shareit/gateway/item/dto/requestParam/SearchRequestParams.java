package ru.practicum.shareit.gateway.item.dto.requestParam;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SearchRequestParams {
    Long userId;
    String text;
    int from;
    int size;
}
