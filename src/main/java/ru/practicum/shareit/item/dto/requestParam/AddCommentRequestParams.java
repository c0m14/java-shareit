package ru.practicum.shareit.item.dto.requestParam;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;

@AllArgsConstructor
public class AddCommentRequestParams {
    Long userId;
    Long itemId;
    CommentDto commentDto;
}
