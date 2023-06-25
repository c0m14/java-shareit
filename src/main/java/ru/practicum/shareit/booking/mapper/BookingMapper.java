package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    public Booking mapToEntity(
            BookingCreationDto bookingCreationDto,
            User booker,
            Item item) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .start(bookingCreationDto.getStart())
                .end(bookingCreationDto.getEnd())
                .build();
    }

    public BookingDto mapToDto(Booking booking,
                               UserBookingDto userBookingDto,
                               ItemBookingDto itemBookingDto) {
        return BookingDto.builder()
                .id(booking.getId())
                .booker(userBookingDto)
                .item(itemBookingDto)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getState())
                .build();
    }

    public List<BookingDto> mapToListDto(List<Booking> bookings) {
        return bookings.stream()
                .map(booking -> {
                    UserBookingDto userBookingDto = userMapper.mapToBookingDto(booking.getBooker());
                    ItemBookingDto itemBookingDto = itemMapper.mapToBookingDto(booking.getItem());

                    return mapToDto(booking, userBookingDto, itemBookingDto);
                })
                .collect(Collectors.toList());
    }

    public LastNextBookingDto mapToLastNextDto(Booking booking) {
        return LastNextBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
