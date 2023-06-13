package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.OnCreateValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(groups = OnCreateValidationGroup.class)
    private String name;
    @NotBlank(groups = OnCreateValidationGroup.class)
    @Email
    private String email;
}
