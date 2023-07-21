package ru.practicum.shareit.gateway.request.controller;

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
import ru.practicum.shareit.gateway.request.client.RequestClient;
import ru.practicum.shareit.gateway.request.dto.CreationRequestDto;
import ru.practicum.shareit.gateway.request.dto.requestParams.GetAllRequestParams;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestControllerRestTemplateImpl.class)
@TestPropertySource(locations = "classpath:test.web.mvc.application.properties")
class RequestControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RequestClient requestClient;
    @Captor
    private ArgumentCaptor<CreationRequestDto> creationRequestDtoArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> userIdArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> requestIDArgumentCaptor;
    @Captor
    private ArgumentCaptor<GetAllRequestParams> getAllRequestParamsArgumentCaptor;

    @SneakyThrows
    @Test
    void add_whenInvoked_thenStatusIsOkAndDtoPassedToClient() {
        Long userId = 0L;
        CreationRequestDto creationRequestDto = new CreationRequestDto();
        creationRequestDto.setDescription("description");

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequestDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestClient, times(1))
                .addRequest(anyLong(), creationRequestDtoArgumentCaptor.capture());

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
    void getAllByOwner_whenInvoked_thenStatusIsOkAndOwnerIdPassedToClient() {
        Long ownerId = 0L;

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(requestClient, times(1))
                .getAllRequestsByOwnerId(userIdArgumentCaptor.capture());
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
    void getAllOtherUsersRequests_whenInvoked_thenStatusIsOkAndParamsPassedToClient() {
        Long ownerId = 0L;
        int from = 0;
        int size = 1;

        mvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(requestClient, times(1))
                .getAllOtherUsersRequests(userIdArgumentCaptor.capture(),
                        getAllRequestParamsArgumentCaptor.capture());
        assertThat(userIdArgumentCaptor.getValue(), equalTo(ownerId));
        assertThat(getAllRequestParamsArgumentCaptor.getValue().getFrom(), equalTo(from));
        assertThat(getAllRequestParamsArgumentCaptor.getValue().getSize(), equalTo(size));
    }

    @SneakyThrows
    @Test
    void getAllOtherUsersRequests_whenFromIsMissing_thenStatusIsOkAndDefaultValuePassedToClient() {
        Long ownerId = 0L;
        int defaultFrom = 0;
        int size = 1;

        mvc.perform(get("/requests/all?size={size}", size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(requestClient, times(1))
                .getAllOtherUsersRequests(userIdArgumentCaptor.capture(),
                        getAllRequestParamsArgumentCaptor.capture());
        assertThat(userIdArgumentCaptor.getValue(), equalTo(ownerId));
        assertThat(getAllRequestParamsArgumentCaptor.getValue().getFrom(), equalTo(defaultFrom));
        assertThat(getAllRequestParamsArgumentCaptor.getValue().getSize(), equalTo(size));
    }

    @SneakyThrows
    @Test
    void getAllOtherUsersRequests_whenSizeIsMissing_thenStatusIsOkAndDefaultValuePassedToClient() {
        Long ownerId = 0L;
        int from = 0;
        int defaultSize = 20;

        mvc.perform(get("/requests/all?from={from}", from)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(requestClient, times(1))
                .getAllOtherUsersRequests(userIdArgumentCaptor.capture(),
                        getAllRequestParamsArgumentCaptor.capture());
        assertThat(userIdArgumentCaptor.getValue(), equalTo(ownerId));
        assertThat(getAllRequestParamsArgumentCaptor.getValue().getFrom(), equalTo(from));
        assertThat(getAllRequestParamsArgumentCaptor.getValue().getSize(), equalTo(defaultSize));
    }

    @SneakyThrows
    @Test
    void getAllOtherUsersRequests_whenFromIsNegative_thenStatusIsBadRequest() {
        Long ownerId = 0L;
        int from = -1;
        int size = 5;

        mvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllOtherUsersRequests_whenSizeIsZero_thenStatusIsBadRequest() {
        Long ownerId = 0L;
        int from = 0;
        int size = 0;

        mvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllOtherUsersRequests_whenSizeIsNegative_thenStatusInternalServerError() {
        Long ownerId = 0L;
        int from = 0;
        int size = -1;

        mvc.perform(get("/requests/all?from={from}@size={size}", from, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void getAllOtherUsersRequests_whenXSharerUserIdHeaderIsMissing_thenStatusIsBadRequest() {
        int from = 0;
        int size = 1;

        mvc.perform(get("/requests/all?from={from}@size={size}", from, size)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getById_whenInvoked_thenStatusIsOkAndParamsPassedToClient() {
        Long userId = 0L;
        Long requestId = 1L;

        mvc.perform(get("/requests/{id}", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestClient, times(1))
                .getRequestById(userIdArgumentCaptor.capture(),
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