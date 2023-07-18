package ru.practicum.shareit.gateway.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.gateway.util.validator.NullOrNotBlank;

import javax.validation.constraints.Size;


@Data
@Builder
public class ItemDto {

    private Long id;

    @NullOrNotBlank
    @Size(max = 50)
    private String name;

    @NullOrNotBlank
    @Size(max = 200)
    private String description;

    private Boolean available;
    private Long requestId;

}
