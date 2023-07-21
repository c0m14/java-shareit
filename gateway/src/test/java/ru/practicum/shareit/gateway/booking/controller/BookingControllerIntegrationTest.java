package ru.practicum.shareit.gateway.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.booking.client.BookingClient;
import ru.practicum.shareit.gateway.booking.dto.BookingCreationDto;
import ru.practicum.shareit.gateway.booking.dto.BookingStateSearchDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingControllerRestTemplateImpl.class)
@TestPropertySource(locations = "classpath:test.web.mvc.application.properties")
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingClient bookingClient;
    @Captor
    private ArgumentCaptor<BookingCreationDto> bookingCreationDtoArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> userIdArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> bookingIdArgumentCaptor;
    @Captor
    private ArgumentCaptor<Boolean> approvedArgumentCaptor;
    @Captor
    private ArgumentCaptor<BookingStateSearchDto> stateArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> fromArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> sizeArgumentCaptor;


    @SneakyThrows
    @Test
    void addBooking_whenInvoked_thenStatusOkAndDtoPassedToClient() {
        Long userId = 0L;
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(0L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreationDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).bookItem(
                userIdArgumentCaptor.capture(),
                bookingCreationDtoArgumentCaptor.capture());
        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when add");
        assertEquals(bookingCreationDto, bookingCreationDtoArgumentCaptor.getValue(),
                "Invalid bookingCreationDto passed to client when add");
    }

    @SneakyThrows
    @Test
    void addBooking_whenItemIdMissing_thenStatusIsBadRequest() {
        Long userId = 0L;
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreationDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addBooking_whenStartIsMissing_thenStatusIsBadRequest() {
        Long userId = 0L;
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(0L)
                .end(LocalDateTime.now().plusHours(2))
                .build();

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreationDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addBooking_whenStartIsInPast_thenStatusIsBadRequest() {
        Long userId = 0L;
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(0L)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreationDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addBooking_whenEndIsInMissing_thenStatusIsBadRequest() {
        Long userId = 0L;
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(0L)
                .start(LocalDateTime.now().plusHours(1))
                .build();

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreationDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addBooking_whenEndIsInThePast_thenStatusIsBadRequest() {
        Long userId = 0L;
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(0L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().minusHours(2))
                .build();

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreationDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addBooking_whenXSharerUserIdHeaderMissing_thenStatusIsBadRequest() {
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(0L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreationDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void changeStatus_whenInvoked_thenStatusIsOkAndParamsPassedToClient() {
        Long userId = 0L;
        Long bookingId = 1L;
        boolean approved = true;

        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", bookingId, approved)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).changeStatus(
                userIdArgumentCaptor.capture(),
                bookingIdArgumentCaptor.capture(),
                approvedArgumentCaptor.capture());

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service");
        assertEquals(bookingId, bookingIdArgumentCaptor.getValue(),
                "Invalid bookingId passed to service");
        assertEquals(approved, approvedArgumentCaptor.getValue(),
                "Invalid approval passed to service");
    }

    @SneakyThrows
    @Test
    void changeStatus_whenApprovedIsMissing_thenInternalServerError() {
        Long userId = 0L;
        Long bookingId = 1L;

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void changeStatus_whenXSharerUserIdHeaderIsMissing_thenStatusIsBadRequest() {
        Long bookingId = 1L;
        boolean approved = true;

        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", bookingId, approved)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getBookingById_whenInvoked_thenStatusIsOkAndParamsPassedToClient() {
        Long userId = 0L;
        Long bookingId = 1L;

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBooking(
                userIdArgumentCaptor.capture(),
                bookingIdArgumentCaptor.capture());
        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service");
        assertEquals(bookingId, bookingIdArgumentCaptor.getValue(),
                "Invalid bookingId passed to service");
    }

    @SneakyThrows
    @Test
    void getBookingById_whenXSharerUserIdIsMissing_thenStatusIsBadRequest() {
        Long bookingId = 1L;

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getBookingsForBooker_whenInvoked_thenStatusIsOkAndParamsPassedToClient() {
        Long userId = 0L;
        String state = "PAST";
        int from = 0;
        int size = 5;

        mvc.perform(get("/bookings?state={state}&from={from}&size={size}", state, from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForBooker(
                userIdArgumentCaptor.capture(),
                stateArgumentCaptor.capture(),
                fromArgumentCaptor.capture(),
                sizeArgumentCaptor.capture());

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service");
        assertEquals(state, stateArgumentCaptor.getValue().toString(),
                "Invalid state passed to service");
        assertEquals(from, fromArgumentCaptor.getValue(),
                "Invalid from passed to service");
        assertEquals(size, sizeArgumentCaptor.getValue(),
                "Invalid size passed to service");
    }

    @SneakyThrows
    @Test
    void getBookingsForBooker_whenStateIsMissing_thenDefaultValuePassedToClient() {
        Long userId = 0L;
        String defaultState = "ALL";
        int from = 0;
        int size = 5;

        mvc.perform(get("/bookings?from={from}&size={size}", from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForBooker(
                anyLong(),
                stateArgumentCaptor.capture(),
                anyInt(),
                anyInt());

        assertEquals(defaultState, stateArgumentCaptor.getValue().toString(),
                "Invalid state passed to service");
    }

    @SneakyThrows
    @Test
    void getBookingsForBooker_whenSizeIsMissing_thenDefaultValuePassedToClient() {
        Long userId = 0L;
        String state = "PAST";
        int from = 0;
        int defaultSize = 20;

        mvc.perform(get("/bookings?state={state}&from={from}", state, from)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForBooker(
                anyLong(),
                any(),
                anyInt(),
                sizeArgumentCaptor.capture());

        assertEquals(defaultSize, sizeArgumentCaptor.getValue(),
                "Invalid size passed to service");
    }

    @SneakyThrows
    @Test
    void getBookingsForBooker_whenFromIsMissing_thenDefaultValuePassedToClient() {
        Long userId = 0L;
        String state = "PAST";
        int defaultFrom = 0;
        int size = 5;

        mvc.perform(get("/bookings?state={state}&size={size}", state, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForBooker(
                anyLong(),
                any(),
                fromArgumentCaptor.capture(),
                anyInt());

        assertEquals(defaultFrom, fromArgumentCaptor.getValue(),
                "Invalid from passed to service");
    }


    @SneakyThrows
    @Test
    void getByStateForItemOwner_whenInvoked_thenStatusIsOkAndParamsPassedToClient() {
        Long userId = 0L;
        String state = "PAST";
        int from = 0;
        int size = 5;

        mvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", state, from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingForItemOwner(
                userIdArgumentCaptor.capture(),
                stateArgumentCaptor.capture(),
                fromArgumentCaptor.capture(),
                sizeArgumentCaptor.capture());

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service");
        assertEquals(state, stateArgumentCaptor.getValue().toString(),
                "Invalid state passed to service");
        assertEquals(from, fromArgumentCaptor.getValue(),
                "Invalid from passed to service");
        assertEquals(size, sizeArgumentCaptor.getValue(),
                "Invalid size passed to service");
    }

    @SneakyThrows
    @Test
    void getByStateForItemOwner_whenStateIsMissing_thenDefaultValuePassedToClient() {
        Long userId = 0L;
        String defaultState = "ALL";
        int from = 0;
        int size = 5;

        mvc.perform(get("/bookings/owner?from={from}&size={size}", from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingForItemOwner(
                anyLong(),
                stateArgumentCaptor.capture(),
                anyInt(),
                anyInt());

        assertEquals(defaultState, stateArgumentCaptor.getValue().toString(),
                "Invalid state passed to service");
    }

    @SneakyThrows
    @Test
    void getByStateForItemOwner_whenSizeIsMissing_thenDefaultValuePassedToClient() {
        Long userId = 0L;
        String state = "PAST";
        int from = 0;
        int defaultSize = 20;

        mvc.perform(get("/bookings/owner?state={state}&from={from}", state, from)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingForItemOwner(
                anyLong(),
                any(),
                anyInt(),
                sizeArgumentCaptor.capture());

        assertEquals(defaultSize, sizeArgumentCaptor.getValue(),
                "Invalid size passed to service");
    }

    @SneakyThrows
    @Test
    void getByStateForItemOwner_whenFromIsMissing_thenDefaultValuePassedToClient() {
        Long userId = 0L;
        String state = "PAST";
        int defaultFrom = 0;
        int size = 5;

        mvc.perform(get("/bookings/owner?state={state}&size={size}", state, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingForItemOwner(
                anyLong(),
                any(),
                fromArgumentCaptor.capture(),
                anyInt());

        assertEquals(defaultFrom, fromArgumentCaptor.getValue(),
                "Invalid from passed to service");
    }
}