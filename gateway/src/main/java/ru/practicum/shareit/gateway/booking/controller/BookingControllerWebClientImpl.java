package ru.practicum.shareit.gateway.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.baseClients.WebClientBase;
import ru.practicum.shareit.gateway.booking.dto.BookingCreationDto;
import ru.practicum.shareit.gateway.booking.dto.BookingStateSearchDto;
import ru.practicum.shareit.gateway.exception.InvalidParamException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
@ConditionalOnProperty(name = "feature.toggles.useWebClient", havingValue = "true")
public class BookingControllerWebClientImpl {

    private final WebClientBase client;
    private static final String BOOKINGS_PATH = "/bookings";

    @PostMapping
    public Mono<ResponseEntity<Object>> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestBody @Valid BookingCreationDto bookingCreationDto) {
        log.info("Creating booking {}, userId={}", bookingCreationDto, userId);
        return client.post(BOOKINGS_PATH, userId, bookingCreationDto);
    }

    @PatchMapping("/{bookingId}")
    public Mono<ResponseEntity<Object>> changeStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable("bookingId") Long bookingId,
                                                     @RequestParam boolean approved
    ) {
        log.info("Got request to change status to booking with: userId: {}, bookingId: {}, approved: {}",
                userId, bookingId, approved);
        return client.patch(BOOKINGS_PATH, userId, bookingId, Map.of("approved", approved));
    }

    @GetMapping("/{bookingId}")
    public Mono<ResponseEntity<Object>> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return client.get(BOOKINGS_PATH, userId, bookingId);
    }

    @GetMapping
    public Mono<ResponseEntity<Object>> getBookingsForBooker(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        BookingStateSearchDto state = BookingStateSearchDto.from(stateParam)
                .orElseThrow(() -> new InvalidParamException("Booking state", "Unknown state: " + stateParam));
        log.info("Get booking  for booker with state {}, userId={}, from={}, size={}",
                stateParam, userId, from, size);
        return client.get(BOOKINGS_PATH, userId, Map.of(
                "state", state,
                "from", from,
                "size", size
        ));
    }

    @GetMapping("/owner")
    public Mono<ResponseEntity<Object>> getByStateForItemOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        BookingStateSearchDto state = BookingStateSearchDto.from(stateParam)
                .orElseThrow(() -> new InvalidParamException("Booking state", "Unknown state: " + stateParam));
        log.info("Get booking for item owner with state {}, userId={}, from={}, size={}",
                stateParam, ownerId, from, size);
        return client.get(BOOKINGS_PATH + "/owner", ownerId, Map.of(
                "state", state,
                "from", from,
                "size", size
        ));
    }
}
