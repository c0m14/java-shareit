package ru.practicum.shareit.item.dto.requestParam;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SearchRequestParams {
    Long userId;
    String text;
    int from;
    int size;
}
