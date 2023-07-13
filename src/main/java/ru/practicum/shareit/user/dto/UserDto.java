package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.validator.NullOrNotBlank;

import javax.validation.constraints.Email;

@Data
@Builder
public class UserDto {
    private Long id;

    @NullOrNotBlank
    private String name;

    @NullOrNotBlank
    @Email
    private String email;
}
