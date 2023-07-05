package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;

import java.util.List;

public interface ItemService {
    ItemDto add(Long userId, ItemCreateDto itemCreateDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemWithBookingsAndCommentsDto getById(Long userId, Long itemId);

    List<ItemWithBookingsAndCommentsDto> getUserItems(Long userId, int from, int size);

    List<ItemDto> searchItems(Long userId, String requestedText, int from, int size);

    CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto);
}
