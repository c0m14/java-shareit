package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserDto add(@RequestBody @Valid UserCreateDto userCreateDto) {
        log.info("Got request to add user {}", userCreateDto);
        return userService.add(userCreateDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(
            @PathVariable("id") Long userId,
            @RequestBody @Valid UserDto userDto) {
        log.info("Got request to update fields: {} to user with id {}", userDto, userId);
        return userService.update(userId, userDto);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable("id") Long id) {
        log.info("Got request to get user with id {}", id);
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Got request to get all users");
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        log.info("Got request to delete user with id {}", id);
        userService.delete(id);
    }
}
