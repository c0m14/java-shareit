package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingServiceImpl bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody @Valid BookingCreationDto bookingCreationDto) {
        log.info("Got request to create booking from user with id {}", userId);
        return bookingService.add(userId, bookingCreationDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable("bookingId") Long bookingId,
                                   @RequestParam boolean approved
    ) {
        log.info("Got request from user with id {} to change status to booking with id {}," +
                " approve decision is {}", userId, bookingId, approved);
        return bookingService.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("bookingId") Long bookingId) {
        log.info("Got request from user with id {} to get booking with id {}", userId, bookingId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getByStateBooker(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Got request from user with id {} to get booking with state {}", bookerId, state);
        return bookingService.getByStateBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByStateOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Got Request from user with id {} to get booking for his items with state {}",
                ownerId,
                state);
        return bookingService.getByStateOwner(ownerId, state);
    }

}
