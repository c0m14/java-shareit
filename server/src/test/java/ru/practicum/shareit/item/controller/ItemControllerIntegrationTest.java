package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIntegrationTest {

    @Captor
    ArgumentCaptor<ItemCreateDto> itemCreateDtoArgumentCaptor;
    @Captor
    ArgumentCaptor<ItemDto> itemDtoArgumentCaptor;
    @Captor
    ArgumentCaptor<Long> userIdArgumentCaptor;
    @Captor
    ArgumentCaptor<Long> itemIdArgumentCaptor;
    @Captor
    ArgumentCaptor<CommentDto> commentDtoArgumentCaptor;
    @Captor
    ArgumentCaptor<Integer> fromParamArgumentCaptor;
    @Captor
    ArgumentCaptor<Integer> sizeParamArgumentCaptor;
    @Captor
    ArgumentCaptor<String> searchTextArgumentCaptor;
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @SneakyThrows
    @Test
    void add_whenInvoked_thenStatusIsOkAndDtoPassedToService() {
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

        verify(itemService, times(1))
                .add(userIdArgumentCaptor.capture(), itemCreateDtoArgumentCaptor.capture());

        assertEquals(itemCreateDto, itemCreateDtoArgumentCaptor.getValue(),
                "Invalid itemDto passed to service when add");
        assertEquals(userId, userIdArgumentCaptor.getValue());
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
    void addComment_whenInvoked_thenStatusIsOkAndCommentPassedToService() {
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
        verify(itemService, times(1))
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
    void update_whenInvoked_thenStatusIsOkAndDtoPassedToService() {
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
        verify(itemService, times(1))
                .update(
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
    void getById_whenInvoked_thenStatusIsOkAndParamsPassedToService() {
        Long userId = 0L;
        Long itemId = 0L;

        mvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemService, times(1))
                .getById(
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
        verify(itemService, times(1))
                .getUserItems(
                        userIdArgumentCaptor.capture(),
                        fromParamArgumentCaptor.capture(),
                        sizeParamArgumentCaptor.capture()
                );

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when getUserItems");
        assertEquals(from, fromParamArgumentCaptor.getValue(),
                "Invalid from param passed to service when getUserItems");
        assertEquals(size, sizeParamArgumentCaptor.getValue(),
                "Invalid size param passed to service when getUserItems");
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
    void searchItems_whenInvoked_thenStatusIsOkAndParamsPassedToService() {
        Long userId = 0L;
        String text = "text";
        int from = 1;
        int size = 2;

        mvc.perform(get("/items/search?text={text}&from={from}&size={size}",
                        text, from, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemService, times(1))
                .searchItems(
                        userIdArgumentCaptor.capture(),
                        searchTextArgumentCaptor.capture(),
                        fromParamArgumentCaptor.capture(),
                        sizeParamArgumentCaptor.capture()
                );

        assertEquals(userId, userIdArgumentCaptor.getValue(),
                "Invalid userId passed to service when getUserItems");
        assertEquals(text, searchTextArgumentCaptor.getValue(),
                "Invalid userId passed to service when getUserItems");
        assertEquals(from, fromParamArgumentCaptor.getValue(),
                "Invalid from param passed to service when getUserItems");
        assertEquals(size, sizeParamArgumentCaptor.getValue(),
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
}