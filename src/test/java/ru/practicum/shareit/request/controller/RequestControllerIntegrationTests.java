package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerIntegrationTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RequestServiceImpl requestService;
    @Captor
    private ArgumentCaptor<CreationRequestDto> creationRequestDtoArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> userIdArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> requestIDArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> fromArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> sizeArgumentCaptor;

    @SneakyThrows
    @Test
    void add_whenInvoked_thenStatusIsOkAndDtoPassedToService() {
        Long userId = 0L;
        CreationRequestDto creationRequestDto = new CreationRequestDto();
        creationRequestDto.setDescription("description");

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequestDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestService, times(1))
                .add(anyLong(), creationRequestDtoArgumentCaptor.capture());

        assertThat(creationRequestDtoArgumentCaptor.getValue(), equalTo(creationRequestDto));
    }

    @SneakyThrows
    @Test
    void add_whenDescriptionIsNull_thenStatusIsBadRequest() {
        Long userId = 0L;
        CreationRequestDto creationRequestDto = new CreationRequestDto();

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequestDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenDescriptionIsBlank_thenStatusIsBadRequest() {
        Long userId = 0L;
        CreationRequestDto creationRequestDto = new CreationRequestDto();
        creationRequestDto.setDescription(" ");

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequestDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenXSharerUserIdHeaderIsMissing_thenStatusIsBadRequest() {
        Long userId = 0L;
        CreationRequestDto creationRequestDto = new CreationRequestDto();
        creationRequestDto.setDescription("description");

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByOwner_whenInvoked_thenStatusIsOkAndOwnerIdPassedToService() {
        Long ownerId = 0L;

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(requestService, times(1))
                .getAllUserItemRequests(userIdArgumentCaptor.capture());
        assertThat(userIdArgumentCaptor.getValue(), equalTo(ownerId));
    }

    @SneakyThrows
    @Test
    void getAllByOwner_whenXSharerHeaderIsMissing_thenStatusIsBadRequest() {

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAll_whenInvoked_thenStatusIsOkAndParamsPassedToService() {
        Long ownerId = 0L;
        int from = 0;
        int size = 1;

        mvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(requestService, times(1))
                .getAllOtherUsersRequests(userIdArgumentCaptor.capture(),
                        fromArgumentCaptor.capture(),
                        sizeArgumentCaptor.capture());
        assertThat(userIdArgumentCaptor.getValue(), equalTo(ownerId));
        assertThat(fromArgumentCaptor.getValue(), equalTo(from));
        assertThat(sizeArgumentCaptor.getValue(), equalTo(size));
    }

    @SneakyThrows
    @Test
    void getAll_whenFromIsMissing_thenStatusIsOkAndDefaultValuePassedToService() {
        Long ownerId = 0L;
        int defaultFrom = 0;
        int size = 1;

        mvc.perform(get("/requests/all?size={size}", size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(requestService, times(1))
                .getAllOtherUsersRequests(userIdArgumentCaptor.capture(),
                        fromArgumentCaptor.capture(),
                        sizeArgumentCaptor.capture());
        assertThat(userIdArgumentCaptor.getValue(), equalTo(ownerId));
        assertThat(fromArgumentCaptor.getValue(), equalTo(defaultFrom));
        assertThat(sizeArgumentCaptor.getValue(), equalTo(size));
    }

    @SneakyThrows
    @Test
    void getAll_whenSizeIsMissing_thenStatusIsOkAndDefaultValuePassedToService() {
        Long ownerId = 0L;
        int from = 0;
        int defaultSize = 20;

        mvc.perform(get("/requests/all?from={from}", from)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(requestService, times(1))
                .getAllOtherUsersRequests(userIdArgumentCaptor.capture(),
                        fromArgumentCaptor.capture(),
                        sizeArgumentCaptor.capture());
        assertThat(userIdArgumentCaptor.getValue(), equalTo(ownerId));
        assertThat(fromArgumentCaptor.getValue(), equalTo(from));
        assertThat(sizeArgumentCaptor.getValue(), equalTo(defaultSize));
    }

    @SneakyThrows
    @Test
    void getAll_whenFromIsNegative_thenStatusIsBadRequest() {
        Long ownerId = 0L;
        int from = -1;
        int size = 5;

        mvc.perform(get("/requests/all?from={from}@size={size}", from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAll_whenSizeIsZero_thenStatusIsBadRequest() {
        Long ownerId = 0L;
        int from = 0;
        int size = 0;

        mvc.perform(get("/requests/all?from={from}@size={size}", from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAll_whenSizeIsNegative_thenStatusIsBadRequest() {
        Long ownerId = 0L;
        int from = 0;
        int size = -1;

        mvc.perform(get("/requests/all?from={from}@size={size}", from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAll_whenXSharerUserIdHeaderIsMissing_thenStatusIsBadRequest() {
        int from = 0;
        int size = 1;

        mvc.perform(get("/requests/all?from={from}@size={size}", from, size)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getById_whenInvoked_thenStatusIsOkAndParamsPassedToService() {
        Long userId = 0L;
        Long requestId = 1L;

        mvc.perform(get("/requests/{id}", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestService, times(1))
                .getById(userIdArgumentCaptor.capture(),
                        requestIDArgumentCaptor.capture());
        assertThat(userIdArgumentCaptor.getValue(), equalTo(userId));
        assertThat(requestIDArgumentCaptor.getValue(), equalTo(requestId));
    }

    @SneakyThrows
    @Test
    void getById_whenXSharerUserIdHeaderIsMissing_thenStatusIsBadRequest() {
        Long requestId = 1L;

        mvc.perform(get("/requests/{id}", requestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}