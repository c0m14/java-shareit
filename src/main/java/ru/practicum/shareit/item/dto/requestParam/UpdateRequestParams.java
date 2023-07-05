package ru.practicum.shareit.item.dto.requestParam;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

@AllArgsConstructor
public class UpdateRequestParams {
    Long userId;
    Long itemId;
    ItemDto itemDto;
}
