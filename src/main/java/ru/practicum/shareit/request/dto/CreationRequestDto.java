package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreationRequestDto {
    @NotBlank
    private String description;
}
