package ru.practicum.shareit.gateway.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.baseClients.WebClientBase;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemCreateDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.item.dto.requestParam.GetByUserRequestParams;
import ru.practicum.shareit.gateway.item.dto.requestParam.SearchRequestParams;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@ConditionalOnProperty(name = "feature.toggles.useWebClient", havingValue = "true")
public class ItemControllerWebClientImpl {

    private final WebClientBase client;
    private static final String ITEMS_PATH = "/items";

    @PostMapping
    public Mono<ResponseEntity<Object>> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestBody @Valid ItemCreateDto itemCreateDto) {
        log.info("Got request to add item with: userId {}, item {}", userId, itemCreateDto);
        return client.post(ITEMS_PATH, userId, itemCreateDto);
    }

    @PostMapping("/{itemId}/comment")
    public Mono<ResponseEntity<Object>> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PathVariable("itemId") Long itemId,
                                                   @RequestBody @Valid CommentDto commentDto) {
        log.info("Got request to add comment to item with: userId {}, itemId {}, comment: {}",
                userId, itemId, commentDto);
        return client.post(ITEMS_PATH + "/" + itemId + "/comment", userId, commentDto);
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<Object>> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable("id") Long itemId,
                                               @RequestBody @Valid ItemDto itemDto) {
        log.info("Got request to edit item with: userId {}, itemId {}, itemUpdate {}", userId, itemId, itemDto);
        return client.patch(ITEMS_PATH, userId, itemId, itemDto);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable("id") Long itemId) {
        log.info("Got request to get item with: userId {}, itemId {}", userId, itemId);
        return client.get(ITEMS_PATH, userId, itemId);
    }

    @GetMapping
    public Mono<ResponseEntity<Object>> getUsersItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size
    ) {
        GetByUserRequestParams requestParams = new GetByUserRequestParams(userId, from, size);
        log.info("Got request to get all items by user with {}", requestParams);
        return client.get(ITEMS_PATH, userId, Map.of(
                "from", from,
                "size", size
        ));
    }

    @GetMapping("/search")
    public Mono<ResponseEntity<Object>> searchItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam("text") String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size
    ) {
        SearchRequestParams requestParams = new SearchRequestParams(userId, text, from, size);
        log.info("Got request to find available items with: {}", requestParams);
        return client.get(ITEMS_PATH + "/search", userId, Map.of(
                "text", text,
                "from", from,
                "size", size
        ));
    }
}
