package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.validator.NullOrNotBlank;

import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
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
