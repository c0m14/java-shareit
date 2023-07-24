package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingServiceImpl bookingService;
    @Captor
    private ArgumentCaptor<BookingCreationDto> bookingCreationDtoArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> userIdArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> bookingIdArgumentCaptor;
    @Captor
    private ArgumentCaptor<Boolean> approvedArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> stateArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> fromArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> sizeArgumentCaptor;


    @SneakyThrows
    @Test
    void add_whenInvoked_thenStatusOkAndDtoPassedToService() {
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

        verify(bookingService, times(1)).add(
                userIdArgumentCaptor.capture(),
                bookingCreationDtoArgumentCaptor.capture());
        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when add");
        assertEquals(bookingCreationDto, bookingCreationDtoArgumentCaptor.getValue(),
                "Invalid bookingCreationDto passed to service when add");
    }

    @SneakyThrows
    @Test
    void add_whenXSharerUserIdHeaderMissing_thenStatusIsBadRequest() {
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
    void changeStatus_whenInvoked_thenStatusIsOkAndParamsPassedToService() {
        Long userId = 0L;
        Long bookingId = 1L;
        boolean approved = true;

        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", bookingId, approved)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).changeStatus(
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
    void changeStatus_whenXSharerUserIdHeaderIsMissing_thenStatusIsBadRequest() {
        Long bookingId = 1L;
        boolean approved = true;

        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", bookingId, approved)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getById_whenInvoked_thenStatusIsOkAndParamsPassedToService() {
        Long userId = 0L;
        Long bookingId = 1L;

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).getById(
                userIdArgumentCaptor.capture(),
                bookingIdArgumentCaptor.capture());
        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service");
        assertEquals(bookingId, bookingIdArgumentCaptor.getValue(),
                "Invalid bookingId passed to service");
    }

    @SneakyThrows
    @Test
    void getById_whenXSharerUserIdIsMissing_thenStatusIsBadRequest() {
        Long bookingId = 1L;

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getByStateForBooker_whenInvoked_thenStatusIsOkAndParamsPassedToService() {
        Long userId = 0L;
        String state = "PAST";
        int from = 0;
        int size = 5;

        mvc.perform(get("/bookings?state={state}&from={from}&size={size}", state, from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).getByStateBooker(
                userIdArgumentCaptor.capture(),
                stateArgumentCaptor.capture(),
                fromArgumentCaptor.capture(),
                sizeArgumentCaptor.capture());

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service");
        assertEquals(state, stateArgumentCaptor.getValue(),
                "Invalid state passed to service");
        assertEquals(from, fromArgumentCaptor.getValue(),
                "Invalid from passed to service");
        assertEquals(size, sizeArgumentCaptor.getValue(),
                "Invalid size passed to service");
    }

    @SneakyThrows
    @Test
    void getByStateForItemOwner_whenInvoked_thenStatusIsOkAndParamsPassedToService() {
        Long userId = 0L;
        String state = "PAST";
        int from = 0;
        int size = 5;

        mvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", state, from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).getByStateOwner(
                userIdArgumentCaptor.capture(),
                stateArgumentCaptor.capture(),
                fromArgumentCaptor.capture(),
                sizeArgumentCaptor.capture());

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service");
        assertEquals(state, stateArgumentCaptor.getValue(),
                "Invalid state passed to service");
        assertEquals(from, fromArgumentCaptor.getValue(),
                "Invalid from passed to service");
        assertEquals(size, sizeArgumentCaptor.getValue(),
                "Invalid size passed to service");
    }
}