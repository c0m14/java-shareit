package ru.practicum.shareit.gateway.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    @NotBlank
    @Size(max = 2000)
    private String text;
    private String authorName;
    private LocalDateTime created;
}
