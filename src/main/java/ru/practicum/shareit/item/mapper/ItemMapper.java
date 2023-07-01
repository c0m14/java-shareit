package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public ItemDto mapToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .itemRequestIds(item.getItemRequests().stream()
                        .mapToLong(ItemRequest::getRequestId)
                        .boxed()
                        .collect(Collectors.toSet()))
                .build();
    }

    public ItemBookingDto mapToBookingDto(Item item) {
        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public Item mapToItem(ItemCreateDto itemCreateDto, User owner) {
        return Item.builder()
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .owner(owner)
                .itemRequests(Set.of(itemCreateDto.getItemRequest()))
                .build();
    }

    public ItemWithBookingsAndCommentsDto mapToWithBookingsDto(
            Item item,
            LastNextBookingDto lastBooking,
            LastNextBookingDto nextBooking,
            List<CommentDto> comments
    ) {
        return ItemWithBookingsAndCommentsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }
}
