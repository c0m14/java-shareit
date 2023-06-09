package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.OnCreateValidationGroup;

import javax.validation.Valid;
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
    @Validated(OnCreateValidationGroup.class)
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody @Valid ItemDto itemDto) {

        log.info("Got request to add item {} from user with id {}", itemDto, userId);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable("id") Long itemId,
                          @RequestBody @Valid ItemDto itemDto) {

        log.info("Got request to edit item {} from user with id {}", itemDto, userId);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable("id") Long itemId) {
        log.info("Got request from user with id {} to get item with id {}", userId, itemId);
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getUsersItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Got request to get all items by user with id {}", userId);
        return itemService.getUsersItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam("text") String text) {
        log.info("Got request to find available items by text:\n\"{}\"", text);
        return itemService.searchItems(userId, text);
    }
}
