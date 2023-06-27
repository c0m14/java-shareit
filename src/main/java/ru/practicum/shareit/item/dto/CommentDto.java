package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {
    Long id;
    @NotBlank
    @Size(max = 2000)
    String text;
    String authorName;
    LocalDateTime created;
}