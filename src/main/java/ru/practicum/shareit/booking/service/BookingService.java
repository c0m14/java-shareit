package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto add(Long bookerId, BookingCreationDto bookingCreationDto);

    BookingDto changeStatus(Long itemOwnerId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getByStateBooker(Long bookerId, String state, int from, int size);

    List<BookingDto> getByStateOwner(Long ownerId, String state, int from, int size);


}
