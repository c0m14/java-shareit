package ru.practicum.shareit.booking.dto.requestParams;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingCreationDto;

@AllArgsConstructor
public class CreateRequestParams {
    Long userId;
    BookingCreationDto bookingCreationDto;
}
