package ru.practicum.shareit.gateway.item.controller;

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
import ru.practicum.shareit.gateway.item.client.ItemClient;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemCreateDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.item.dto.requestParam.GetByUserRequestParams;
import ru.practicum.shareit.gateway.item.dto.requestParam.SearchRequestParams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemControllerRestTemplateImpl.class)
@TestPropertySource(locations = "classpath:test.web.mvc.application.properties")
class ItemControllerIntegrationTest {

    @Captor
    private ArgumentCaptor<ItemCreateDto> itemCreateDtoArgumentCaptor;
    @Captor
    private ArgumentCaptor<ItemDto> itemDtoArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> userIdArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> itemIdArgumentCaptor;
    @Captor
    private ArgumentCaptor<CommentDto> commentDtoArgumentCaptor;
    @Captor
    private ArgumentCaptor<GetByUserRequestParams> getByUserRequestParamsArgumentCaptor;
    @Captor
    private ArgumentCaptor<SearchRequestParams> searchRequestParamsArgumentCaptor;
    @MockBean
    private ItemClient itemClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @SneakyThrows
    @Test
    void add_whenInvoked_thenStatusIsOkAndDtoPassedToClient() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("item")
                .description("text")
                .available(true)
                .build();
        Long userId = 0L;

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient, times(1))
                .addItem(userIdArgumentCaptor.capture(),
                        itemCreateDtoArgumentCaptor.capture());

        assertEquals(itemCreateDto, itemCreateDtoArgumentCaptor.getValue(),
                "Invalid itemDto passed to service when add");
        assertEquals(userId, userIdArgumentCaptor.getValue());
    }

    @SneakyThrows
    @Test
    void add_whenXSharedUserIdHeaderMissed_thenStatusIsBadRequest() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("item")
                .description("text")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenNameIsBlank_thenStatusIsBadRequest() {
        Long userId = 0L;
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name(" ")
                .description("text")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenNameIsAbsent_thenStatusIsBadRequest() {
        Long userId = 0L;
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .description("text")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenNameLongerThan50_thenStatusIsBadRequest() {
        Long userId = 0L;
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("*".repeat(51))
                .description("text")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenDescriptionIsBlank_thenStatusIsBadRequest() {
        Long userId = 0L;
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("item")
                .description(" ")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenDescriptionIsAbsent_thenStatusIsBadRequest() {
        Long userId = 0L;
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("item")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenDescriptionLongerThan200_thenStatusIsBadRequest() {
        Long userId = 0L;
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("item")
                .description("*".repeat(201))
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenAvailableIsAbsent_thenStatusIsBadRequest() {
        Long userId = 0L;
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("item")
                .description("description")
                .build();

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenWithRequestId_thenStatusIsOk() {
        Long userId = 0L;
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .requestId(0L)
                .build();

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void addComment_whenInvoked_thenStatusIsOkAndCommentPassedToClient() {
        Long userId = 0L;
        Long itemId = 0L;
        CommentDto comment = CommentDto.builder()
                .text("text")
                .authorName("authorName")
                .build();

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemClient, times(1))
                .addComment(
                        userIdArgumentCaptor.capture(),
                        itemIdArgumentCaptor.capture(),
                        commentDtoArgumentCaptor.capture()
                );

        assertEquals(comment, commentDtoArgumentCaptor.getValue(),
                "Invalid commentDto passed to service when addComment");
        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when addComment");
        assertEquals(itemId, itemIdArgumentCaptor.getValue(),
                "Invalid itemId passed to service when addComment");
    }

    @SneakyThrows
    @Test
    void addComment_whenXSharedUserIdHeaderMissing_thenStatusIsBadRequest() {
        Long itemId = 0L;
        CommentDto comment = CommentDto.builder()
                .text("text")
                .authorName("authorName")
                .build();

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addComment_whenTextIsBlank_thenStatusIsBadRequest() {
        Long userId = 0L;
        Long itemId = 0L;
        CommentDto comment = CommentDto.builder()
                .text(" ")
                .authorName("authorName")
                .build();

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addComment_whenTextIsAbsent_thenStatusIsBadRequest() {
        Long userId = 0L;
        Long itemId = 0L;
        CommentDto comment = CommentDto.builder()
                .authorName("authorName")
                .build();

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addComment_whenTextLongerThan2000_thenStatusIsBadRequest() {
        Long userId = 0L;
        Long itemId = 0L;
        CommentDto comment = CommentDto.builder()
                .text("*".repeat(2001))
                .authorName("authorName")
                .build();

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenInvoked_thenStatusIsOkAndDtoPassedToClient() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto updatedItem = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        mvc.perform(patch("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemClient, times(1))
                .updateItem(
                        userIdArgumentCaptor.capture(),
                        itemIdArgumentCaptor.capture(),
                        itemDtoArgumentCaptor.capture()
                );

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when update");
        assertEquals(itemId, itemIdArgumentCaptor.getValue(),
                "Invalid itemId passed to service when update");
        assertEquals(updatedItem, itemDtoArgumentCaptor.getValue(),
                "Invalid itemDto passed to service when update");
    }

    @SneakyThrows
    @Test
    void update_whenOnlyName_thenStatusIsOk() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto updatedItem = ItemDto.builder()
                .name("name")
                .build();

        mvc.perform(patch("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void update_whenOnlyDescription_thenStatusIsOk() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto updatedItem = ItemDto.builder()
                .description("description")
                .build();

        mvc.perform(patch("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void update_whenOnlyRequestId_thenStatusIsOk() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto updatedItem = ItemDto.builder()
                .requestId(0L)
                .build();

        mvc.perform(patch("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void update_whenOnlyAvailable_thenStatusIsOk() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto updatedItem = ItemDto.builder()
                .available(false)
                .build();

        mvc.perform(patch("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void update_whenXSharedUserIdHeaderMissed_thenStatusIsBadRequest() {
        Long itemId = 0L;
        ItemDto updatedItem = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        mvc.perform(patch("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenNameIsBlank_thenStatusIsBadRequest() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto updatedItem = ItemDto.builder()
                .name(" ")
                .description("description")
                .available(true)
                .build();

        mvc.perform(patch("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenNameIsLongerThan50_thenStatusIsBadRequest() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto updatedItem = ItemDto.builder()
                .name("*".repeat(51))
                .description("description")
                .available(true)
                .build();

        mvc.perform(patch("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenDescriptionIsBlank_thenStatusIsBadRequest() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto updatedItem = ItemDto.builder()
                .name("name")
                .description(" ")
                .available(true)
                .build();

        mvc.perform(patch("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenDescriptionIsLongerThan200_thenStatusIsBadRequest() {
        Long userId = 0L;
        Long itemId = 0L;
        ItemDto updatedItem = ItemDto.builder()
                .name("name")
                .description("*".repeat(201))
                .available(true)
                .build();

        mvc.perform(patch("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getById_whenInvoked_thenStatusIsOkAndParamsPassedToClient() {
        Long userId = 0L;
        Long itemId = 0L;

        mvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemClient, times(1))
                .getItemById(
                        userIdArgumentCaptor.capture(),
                        itemIdArgumentCaptor.capture()
                );

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when update");
        assertEquals(itemId, itemIdArgumentCaptor.getValue(),
                "Invalid itemId passed to service when update");
    }

    @SneakyThrows
    @Test
    void getById_whenXSharedUserIdHeaderIsAbsent_thenStatusIsBadRequest() {
        Long itemId = 0L;

        mvc.perform(get("/items/{id}", itemId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getUsersItems_whenInvoked_thenStatusIsOkAndParamsPassedToService() {
        Long userId = 0L;
        int from = 1;
        int size = 2;

        mvc.perform(get("/items?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemClient, times(1))
                .getUsersItems(
                        userIdArgumentCaptor.capture(),
                        getByUserRequestParamsArgumentCaptor.capture()
                );

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when getUserItems");
        assertEquals(from, getByUserRequestParamsArgumentCaptor.getValue().getFrom(),
                "Invalid from param passed to service when getUserItems");
        assertEquals(size, getByUserRequestParamsArgumentCaptor.getValue().getSize(),
                "Invalid size param passed to service when getUserItems");
    }

    @SneakyThrows
    @Test
    void getUsersItems_whenFromIsAbsent_thenStatusIsOkAndDefaultValueUsed() {
        Long userId = 0L;
        int fromDefault = 0;
        int size = 1;

        mvc.perform(get("/items?&size={size}", size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemClient, times(1))
                .getUsersItems(
                        userIdArgumentCaptor.capture(),
                        getByUserRequestParamsArgumentCaptor.capture()
                );

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when getUserItems");
        assertEquals(fromDefault, getByUserRequestParamsArgumentCaptor.getValue().getFrom(),
                "Invalid from param passed to service when getUserItems");
        assertEquals(size, getByUserRequestParamsArgumentCaptor.getValue().getSize(),
                "Invalid size param passed to service when getUserItems");
    }

    @SneakyThrows
    @Test
    void getUsersItems_whenSizeIsAbsent_thenStatusIsOkAndDefaultValueIsUsed() {
        Long userId = 0L;
        int from = 1;
        int defaultSize = 20;

        mvc.perform(get("/items?from={from}", from)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemClient, times(1))
                .getUsersItems(
                        userIdArgumentCaptor.capture(),
                        getByUserRequestParamsArgumentCaptor.capture()
                );

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when getUserItems");
        assertEquals(from, getByUserRequestParamsArgumentCaptor.getValue().getFrom(),
                "Invalid from param passed to service when getUserItems");
        assertEquals(defaultSize, getByUserRequestParamsArgumentCaptor.getValue().getSize(),
                "Invalid size param passed to service when getUserItems");
    }

    @SneakyThrows
    @Test
    void getUsersItems_whenFromIsNegative_thenStatusIsBadRequest() {
        Long userId = 0L;
        int from = -1;
        int size = 2;

        mvc.perform(get("/items?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getUsersItems_whenSizeIsZero_thenStatusIsBadRequest() {
        Long userId = 0L;
        int from = 1;
        int size = 0;

        mvc.perform(get("/items?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getUsersItems_whenSizeIsNegative_thenStatusIsBadRequest() {
        Long userId = 0L;
        int from = 1;
        int size = -1;

        mvc.perform(get("/items?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getUsersItems_whenXSharedUserIdHeaderIsAbsent_thenStatusIsBadRequest() {
        Long userId = 0L;
        int from = 1;
        int size = 2;

        mvc.perform(get("/items?from={from}&size={size}", from, size))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void searchItems_whenInvoked_thenStatusIsOkAndParamsPassedToClient() {
        Long userId = 0L;
        String text = "text";
        int from = 1;
        int size = 2;

        mvc.perform(get("/items/search?text={text}&from={from}&size={size}",
                        text, from, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemClient, times(1))
                .searchItems(
                        userIdArgumentCaptor.capture(),
                        searchRequestParamsArgumentCaptor.capture()
                );

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when getUserItems");
        assertEquals(text, searchRequestParamsArgumentCaptor.getValue().getText(),
                "Invalid userId passed to service when getUserItems");
        assertEquals(from, searchRequestParamsArgumentCaptor.getValue().getFrom(),
                "Invalid from param passed to service when getUserItems");
        assertEquals(size, searchRequestParamsArgumentCaptor.getValue().getSize(),
                "Invalid size param passed to service when getUserItems");
    }

    @SneakyThrows
    @Test
    void searchItems_whenFromIsAbsent_thenStatusIsOkAndDefaultValueUsed() {
        Long userId = 0L;
        String text = "text";
        int fromDefault = 0;
        int size = 2;

        mvc.perform(get("/items/search?text={text}&size={size}",
                        text, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemClient, times(1))
                .searchItems(
                        userIdArgumentCaptor.capture(),
                        searchRequestParamsArgumentCaptor.capture()
                );

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when getUserItems");
        assertEquals(text, searchRequestParamsArgumentCaptor.getValue().getText(),
                "Invalid userId passed to service when getUserItems");
        assertEquals(fromDefault, searchRequestParamsArgumentCaptor.getValue().getFrom(),
                "Invalid from param passed to service when getUserItems");
        assertEquals(size, searchRequestParamsArgumentCaptor.getValue().getSize(),
                "Invalid size param passed to service when getUserItems");
    }

    @SneakyThrows
    @Test
    void searchItems_whenSizeIsAbsent_thenStatusIsOkAndDefaultValueUsed() {
        Long userId = 0L;
        String text = "text";
        int from = 1;
        int sizeDefault = 20;

        mvc.perform(get("/items/search?text={text}&from={from}",
                        text, from)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemClient, times(1))
                .searchItems(
                        userIdArgumentCaptor.capture(),
                        searchRequestParamsArgumentCaptor.capture()
                );

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when getUserItems");
        assertEquals(text, searchRequestParamsArgumentCaptor.getValue().getText(),
                "Invalid userId passed to service when getUserItems");
        assertEquals(from, searchRequestParamsArgumentCaptor.getValue().getFrom(),
                "Invalid from param passed to service when getUserItems");
        assertEquals(sizeDefault, searchRequestParamsArgumentCaptor.getValue().getSize(),
                "Invalid size param passed to service when getUserItems");
    }

    @SneakyThrows
    @Test
    void searchItems_whenXSharerUserIdHeaderIsAbsent_thenStatusIsBadRequest() {
        String text = "text";
        int from = 1;
        int size = 2;

        mvc.perform(get("/items/search?text={text}&from={from}&size={size}",
                        text, from, size))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void searchItems_whenTextIsAbsent_thenInternalServerError() {
        Long userId = 0L;
        int from = 1;
        int size = 2;

        mvc.perform(get("/items/search?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void searchItems_whenFromIsNegative_thenStatusIsBadRequest() {
        Long userId = 0L;
        String text = "text";
        int from = -1;
        int size = 2;

        mvc.perform(get("/items/search?text={text}&from={from}&size={size}",
                        text, from, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void searchItems_whenSizeIsZero_thenStatusIsBadRequest() {
        Long userId = 0L;
        String text = "text";
        int from = 1;
        int size = 0;

        mvc.perform(get("/items/search?text={text}&from={from}&size={size}",
                        text, from, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void searchItems_whenSizeIsNegative_thenStatusIsBadRequest() {
        String text = "text";
        Long userId = 0L;
        int from = 1;
        int size = -1;

        mvc.perform(get("/items/search?text={text}&from={from}&size={size}",
                        text, from, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }
}