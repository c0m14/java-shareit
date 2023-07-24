package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestNoItemsDto;
import ru.practicum.shareit.request.dto.requestParams.GetAllRequestParams;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestNoItemsDto add(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                 @RequestBody CreationRequestDto requestDto) {
        log.info("Got request to add item request with: ownerId {}, request {}", ownerId, requestDto);
        return requestService.add(ownerId, requestDto);
    }

    @GetMapping
    public List<RequestDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {

        log.info("Got request to get item requests for user with id {}", ownerId);
        return requestService.getAllUserItemRequests(ownerId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                     @RequestParam(value = "from") int from,
                                                     @RequestParam(value = "size") int size
    ) {

        GetAllRequestParams requestParams = new GetAllRequestParams(ownerId, from, size);
        log.info("Got request to get all item requests with: {}", requestParams);
        return requestService.getAllOtherUsersRequests(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public RequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("id") Long requestId) {
        log.info("Got request to get request with: userId {}, requestId {}", userId, requestId);
        return requestService.getById(userId, requestId);
    }
}
