package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestNoItemsDto;
import ru.practicum.shareit.request.dto.requestParams.CreateRequestParams;
import ru.practicum.shareit.request.dto.requestParams.GetAllRequestParams;
import ru.practicum.shareit.request.dto.requestParams.GetByIdRequestParams;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestNoItemsDto add(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                 @RequestBody @Valid CreationRequestDto requestDto) {
        CreateRequestParams requestParams = new CreateRequestParams(ownerId, requestDto);

        log.info("Got request to add item request with: {}", requestParams);
        return requestService.add(ownerId, requestDto);
    }

    @GetMapping
    public List<RequestDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {

        log.info("Got request to get item requests for user with id {}", ownerId);
        return requestService.getAllUserItemRequests(ownerId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                   @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                   @RequestParam(value = "size", defaultValue = "20") @Min(1) int size
    ) {

        GetAllRequestParams requestParams = new GetAllRequestParams(ownerId, from, size);
        log.info("Got request to get all item requests with: {}", requestParams);
        return requestService.getAll(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public RequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("id") Long requestId) {

        GetByIdRequestParams requestParams = new GetByIdRequestParams(userId, requestId);
        log.info("Got request to get request with: {}", requestParams);
        return requestService.getById(userId, requestId);

    }
}
