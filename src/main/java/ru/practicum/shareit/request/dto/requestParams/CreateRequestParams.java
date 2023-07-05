package ru.practicum.shareit.request.dto.requestParams;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.request.dto.CreationRequestDto;

@AllArgsConstructor
public class CreateRequestParams {
    Long userId;
    CreationRequestDto creationRequestDto;
}
