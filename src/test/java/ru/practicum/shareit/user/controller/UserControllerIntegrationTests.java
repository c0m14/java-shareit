package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIntegrationTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    @Captor
    private ArgumentCaptor<UserCreateDto> userCreateDtoArgumentCaptor;
    @Captor
    private ArgumentCaptor<UserDto> userDtoArgumentCaptor;


    @SneakyThrows
    @Test
    void add_whenInvoked_thenStatusCodeIsOkAndDtoPassedToService() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("name")
                .email("email@test.ru")
                .build();

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).add(userCreateDto);
        verify(userService).add(userCreateDtoArgumentCaptor.capture());
        assertEquals(userCreateDto, userCreateDtoArgumentCaptor.getValue(),
                "Invalid user passed to User Service when add");
    }

    @SneakyThrows
    @Test
    void add_whenNameIsBlank_thenStatusCodeIsBadRequest() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name(" ")
                .email("email@test.ru")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenNameIsNull_thenStatusCodeIsBadRequest() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .email("email@test.ru")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenEmailIsBlank_thenStatusCodeIsBadRequest() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("name")
                .email(" ")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenEmailIsNull_thenStatusCodeIsBadRequest() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("name")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenEmailIsNotValid_thenStatusCodeIsBadRequest() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("name")
                .email("wrongEmail")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenInvoked_thenStatusIsOkAndDtoPassedToService() {
        Long userId = 0L;
        UserDto userDto = UserDto.builder()
                .id(userId)
                .name("name")
                .email("email@test.ru")
                .build();

        mvc.perform(patch("/users/{id}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).update(userId, userDto);
        verify(userService).update(anyLong(), userDtoArgumentCaptor.capture());
        assertEquals(userDto, userDtoArgumentCaptor.getValue(),
                "Invalid user passed to User Service when update");
    }

    @SneakyThrows
    @Test
    void update_whenNameIsBlank_thenStatusCodeIsBadRequest() {
        Long userId = 0L;
        UserDto userDto = UserDto.builder()
                .name(" ")
                .build();

        mvc.perform(patch("/users/{id}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenEmailIsBlank_thenStatusCodeIsBadRequest() {
        Long userId = 0L;
        UserDto userDto = UserDto.builder()
                .email(" ")
                .build();

        mvc.perform(patch("/users/{id}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenEmailIsNotValidFormat_thenStatusCodeIsBadRequest() {
        Long userId = 0L;
        UserDto userDto = UserDto.builder()
                .email("email")
                .build();

        mvc.perform(patch("/users/{id}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void get_whenInvoked_thenStatusIsOkAndIdPassedToService() {
        mvc.perform((get("/users/{id}", anyLong())))
                .andExpect(status().isOk());
        verify(userService, times(1)).getById(anyLong());
    }

    @SneakyThrows
    @Test
    void getAll_whenInvoked_thenStatusIsOkAndIdPassedToService() {
        mvc.perform((get("/users")))
                .andExpect(status().isOk());
        verify(userService, times(1)).getAll();
    }

    @SneakyThrows
    @Test
    void delete_whenInvoked_thenStatusIsOkAndIdPassedToService() {
        mvc.perform((delete("/users/{id}", anyLong())))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(anyLong());
    }
}