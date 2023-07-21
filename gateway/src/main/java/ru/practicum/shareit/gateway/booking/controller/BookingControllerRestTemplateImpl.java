package ru.practicum.shareit.gateway.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.booking.client.BookingClient;
import ru.practicum.shareit.gateway.booking.dto.BookingCreationDto;
import ru.practicum.shareit.gateway.booking.dto.BookingStateSearchDto;
import ru.practicum.shareit.gateway.exception.InvalidParamException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
@ConditionalOnProperty(name = "feature.toggles.useWebClient", havingValue = "false")
public class BookingControllerRestTemplateImpl {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid BookingCreationDto bookingCreationDto) {
        log.info("Creating booking {}, userId={}", bookingCreationDto, userId);
        return bookingClient.bookItem(userId, bookingCreationDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable("bookingId") Long bookingId,
                                               @RequestParam boolean approved
    ) {
        log.info("Got request to change status to booking with: userId: {}, bookingId: {}, approved: {}",
                userId, bookingId, approved);
        return bookingClient.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsForBooker(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        BookingStateSearchDto state = BookingStateSearchDto.from(stateParam)
                .orElseThrow(() -> new InvalidParamException("Booking state", "Unknown state: " + stateParam));
        log.info("Get booking  for booker with state {}, userId={}, from={}, size={}",
                stateParam, userId, from, size);
        return bookingClient.getBookingsForBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByStateForItemOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        BookingStateSearchDto state = BookingStateSearchDto.from(stateParam)
                .orElseThrow(() -> new InvalidParamException("Booking state", "Unknown state: " + stateParam));
        log.info("Get booking for item owner with state {}, userId={}, from={}, size={}",
                stateParam, ownerId, from, size);
        return bookingClient.getBookingForItemOwner(ownerId, state, from, size);
    }
}
