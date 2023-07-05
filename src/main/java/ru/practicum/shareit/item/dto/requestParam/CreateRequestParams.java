package ru.practicum.shareit.item.dto.requestParam;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.item.dto.ItemCreateDto;

@AllArgsConstructor
public class CreateRequestParams {
    Long userId;
    ItemCreateDto itemCreateDto;
}
