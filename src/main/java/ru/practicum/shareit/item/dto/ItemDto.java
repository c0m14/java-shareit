package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {

    private Long id;
    @Size(max = 50)
    private String name;
    @Size(max = 200)
    private String description;
    private Boolean available;
    private Long requestId;

}
