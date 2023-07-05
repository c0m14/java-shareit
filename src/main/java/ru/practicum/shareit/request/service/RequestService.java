package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestNoItemsDto;

import java.util.List;

public interface RequestService {
    RequestNoItemsDto add(Long userId, CreationRequestDto creationRequestDto);

    List<RequestDto> getAllUserItemRequests(Long ownerId);

    List<RequestDto> getAll(Long userId, int from, int size);

    RequestDto getById(Long userId, Long requestId);
}
