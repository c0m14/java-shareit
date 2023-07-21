package ru.practicum.shareit.gateway.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.baseClients.BaseClient;
import ru.practicum.shareit.gateway.booking.dto.BookingCreationDto;
import ru.practicum.shareit.gateway.booking.dto.BookingStateSearchDto;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String BOOKINGS_API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + BOOKINGS_API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookingsForBooker(long userId, BookingStateSearchDto state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingForItemOwner(long userId, BookingStateSearchDto state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> bookItem(long userId, BookingCreationDto bookingCreationDto) {
        return post("", userId, bookingCreationDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> changeStatus(long userId, Long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }
}
