package ru.practicum.shareit.gateway.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.user.client.UserClient;
import ru.practicum.shareit.gateway.user.dto.UserCreateDto;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import javax.validation.Valid;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@ConditionalOnProperty(name = "feature.toggles.useWebClient", havingValue = "false")
public class UserControllerRestTemplateImpl {

    private final UserClient userClient;

    @PostMapping()
    public ResponseEntity<Object> add(@RequestBody @Valid UserCreateDto userCreateDto) {
        log.info("Got request to add user {}", userCreateDto);
        return userClient.addUser(userCreateDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable("id") Long userId,
            @RequestBody @Valid UserDto userDto) {
        log.info("Got request to update fields: {} to user with id {}", userDto, userId);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable("id") Long id) {
        log.info("Got request to get user with id {}", id);
        return userClient.getUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Got request to get all users");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        log.info("Got request to delete user with id {}", id);
        return userClient.deleteUserById(id);
    }
}
