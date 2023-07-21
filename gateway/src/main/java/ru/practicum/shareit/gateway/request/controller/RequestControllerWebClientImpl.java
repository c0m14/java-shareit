package ru.practicum.shareit.gateway.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.baseClients.WebClientBase;
import ru.practicum.shareit.gateway.request.dto.CreationRequestDto;
import ru.practicum.shareit.gateway.request.dto.requestParams.GetAllRequestParams;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
@ConditionalOnProperty(name = "feature.toggles.useWebClient", havingValue = "true")
public class RequestControllerWebClientImpl {

    private final WebClientBase client;
    private static final String REQUESTS_PATH = "/requests";

    @PostMapping
    public Mono<ResponseEntity<Object>> add(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                            @RequestBody @Valid CreationRequestDto requestDto) {
        log.info("Got request to add item request with: ownerId {}, request {}", ownerId, requestDto);
        return client.post(REQUESTS_PATH, ownerId, requestDto);
    }

    @GetMapping
    public Mono<ResponseEntity<Object>> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {

        log.info("Got request to get item requests for user with id {}", ownerId);
        return client.get(REQUESTS_PATH, ownerId);
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<Object>> getAllOtherUsersRequests(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "20") Integer size
    ) {

        GetAllRequestParams requestParams = new GetAllRequestParams(ownerId, from, size);
        log.info("Got request to get all item requests with: {}", requestParams);
        return client.get(REQUESTS_PATH + "/all", ownerId, Map.of(
                "from", from,
                "size", size
        ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable("id") Long requestId) {
        log.info("Got request to get request with: userId {}, requestId {}", userId, requestId);
        return client.get(REQUESTS_PATH, userId, requestId);
    }
}
