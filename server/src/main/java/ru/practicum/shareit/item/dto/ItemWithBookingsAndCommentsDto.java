package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;

import java.util.List;

@Getter
@Setter
@Builder
public class ItemWithBookingsAndCommentsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private LastNextBookingDto lastBooking;
    private LastNextBookingDto nextBooking;
    private List<CommentDto> comments;
}
