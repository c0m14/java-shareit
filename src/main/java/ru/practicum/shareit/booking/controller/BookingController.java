package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.requestParams.GetByStateRequestParams;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

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
        log.info("Got request to create booking with userId: {}, booking: {}", userId, bookingCreationDto);
        return bookingService.add(userId, bookingCreationDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable("bookingId") Long bookingId,
                                   @RequestParam boolean approved
    ) {
        log.info("Got request to change status to booking with: userId: {}, bookingId: {}, approved: {}",
                userId, bookingId, approved);
        return bookingService.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("bookingId") Long bookingId) {
        log.info("Got request to get booking with: userId {}, bookingId {}", userId, bookingId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getByStateForBooker(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                @RequestParam(name = "size", defaultValue = "20") @Min(1) int size) {
        GetByStateRequestParams requestParams = new GetByStateRequestParams(bookerId, state, from, size);
        log.info("Got request from booker with to get bookings: {}", requestParams);
        return bookingService.getByStateBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByStateForItemOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                   @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(name = "size", defaultValue = "20") @Min(1) int size) {
        GetByStateRequestParams requestParams = new GetByStateRequestParams(ownerId, state, from, size);
        log.info("Got request from items owner to get bookings: {}", requestParams);
        return bookingService.getByStateOwner(ownerId, state, from, size);
    }

}
