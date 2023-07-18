package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    public UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserBookingDto mapToBookingDto(User user) {
        return UserBookingDto.builder()
                .id(user.getId())
                .build();
    }

    public User mapToUser(UserCreateDto userCreateDto) {
        return User.builder()
                .name(userCreateDto.getName())
                .email(userCreateDto.getEmail())
                .build();
    }
}
