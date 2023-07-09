package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.dto.requestParam.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody @Valid ItemCreateDto itemCreateDto) {

        CreateRequestParams requestParams = new CreateRequestParams(userId, itemCreateDto);
        log.info("Got request to add item with: {}", requestParams);
        return itemService.add(userId, itemCreateDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable("itemId") Long itemId,
                                 @RequestBody @Valid CommentDto commentDto) {

        AddCommentRequestParams requestParams = new AddCommentRequestParams(userId, itemId, commentDto);
        log.info("Got request to add comment to item with: {}", requestParams);
        return itemService.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable("id") Long itemId,
                          @RequestBody @Valid ItemDto itemDto) {

        UpdateRequestParams requestParams = new UpdateRequestParams(userId, itemId, itemDto);
        log.info("Got request to edit item with: {}", requestParams);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemWithBookingsAndCommentsDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable("id") Long itemId) {

        GetByIdRequestParams requestParams = new GetByIdRequestParams(userId, itemId);
        log.info("Got request to get item with: {}", requestParams);
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingsAndCommentsDto> getUsersItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) int size
    ) {

        GetByUserRequestParams requestParams = new GetByUserRequestParams(userId, from, size);
        log.info("Got request to get all items by user with id {}", userId);
        return itemService.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam("text") String text,
                                     @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                     @RequestParam(name = "size", defaultValue = "20") @Min(1) int size
    ) {

        SearchRequestParams requestParams = new SearchRequestParams(userId, text, from, size);
        log.info("Got request to find available items with: {}", requestParams);
        return itemService.searchItems(userId, text, from, size);
    }
}
