package ru.practicum.shareit.gateway.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.baseClients.WebClientBase;
import ru.practicum.shareit.gateway.user.dto.UserCreateDto;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@Validated
@RequiredArgsConstructor
@ConditionalOnProperty(name = "feature.toggles.useWebClient", havingValue = "true")
public class UserControllerWebClientImpl {
    private final WebClientBase client;
    private static final String USERS_PATH = "/users";

    @PostMapping()
    public Mono<ResponseEntity<Object>> add(@RequestBody @Valid UserCreateDto userCreateDto) {
        log.info("Got request to add user {}", userCreateDto);
        return client.post(USERS_PATH, userCreateDto);
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<Object>> update(
            @PathVariable("id") Long userId,
            @RequestBody @Valid UserDto userDto) {
        log.info("Got request to update fields: {} to user with id {}", userDto, userId);
        return client.patch(USERS_PATH, userId, userDto);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> get(@PathVariable("id") Long id) {
        log.info("Got request to get user with id {}", id);
        return client.get(USERS_PATH, id);
    }

    @GetMapping
    public Mono<ResponseEntity<Object>> getAll() {
        log.info("Got request to get all users");
        return client.get(USERS_PATH);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable("id") Long id) {
        log.info("Got request to delete user with id {}", id);
        return client.delete(USERS_PATH, id);
    }
}
