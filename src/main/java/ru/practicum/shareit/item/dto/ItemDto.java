package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.OnCreateValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {

    private Long id;
    @NotBlank(groups = OnCreateValidationGroup.class)
    @Size(max = 50)
    private String name;
    @NotBlank(groups = OnCreateValidationGroup.class)
    @Size(max = 200)
    private String description;
    @NotNull(groups = OnCreateValidationGroup.class)
    private Boolean available;
    private Long itemRequestId;

}
