package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestNoItemsDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    private final ItemMapper itemMapper;

    public Request mapToEntity(CreationRequestDto creationRequestDto, User owner) {
        return Request.builder()
                .owner(owner)
                .description(creationRequestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public RequestDto mapToRequestDto(Request request, List<ItemDto> requestItems) {
        return RequestDto.builder()
                .id(request.getRequestId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(requestItems)
                .build();
    }

    public RequestNoItemsDto mapToNoItemsDto(Request request) {
        return RequestNoItemsDto.builder()
                .id(request.getRequestId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public List<RequestDto> mapStreamToDto(Stream<Request> requestStream, ItemRepository itemRepository) {
        return requestStream
                .filter(Objects::nonNull)
                .map(itemRequest -> {
                    List<ItemDto> requestItemsList =
                            itemRepository.findAllByRequest_RequestId(itemRequest.getRequestId())
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .map(item -> itemMapper.mapToDto(item, itemRequest.getRequestId()))
                                    .collect(Collectors.toList());

                    return mapToRequestDto(itemRequest, requestItemsList);
                })
                .collect(Collectors.toList());
    }
}
