package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class UserServiceImplIntegrationTest {
    @Autowired
    private UserServiceImpl userService;
    @MockBean
    private UserRepository userRepository;
    @Captor
    ArgumentCaptor<User> userArgumentCaptor;


    @Test
    void add_whenInvoked_thenMappedFromCreateDtoToEntityAndPassedToRepository() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("name")
                .email("email")
                .build();
        User expectedUser = User.builder()
                .name("name")
                .email("email")
                .build();
        when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(getValidUser());

        userService.add(userCreateDto);
        verify(userRepository).save(userArgumentCaptor.capture());

        assertEquals(expectedUser, userArgumentCaptor.getValue(),
                "Invalid mapping Create Dto -> User when add"
        );
    }

    @Test
    void add_whenSaved_thenMappedFromEntityToUserDto() {
        User savedUser = User.builder()
                .id(0L)
                .name("name")
                .email("email")
                .build();
        UserDto expectedUserDto = UserDto.builder()
                .id(0L)
                .name("name")
                .email("email")
                .build();
        when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(savedUser);

        UserDto returnedDto = userService.add(getValidCreateDto());

        assertEquals(expectedUserDto, returnedDto,
                "Invalid mapping User -> UserDto when add");
    }

    @Test
    void update_whenInvoked_thenMappedToEntityAndPassedToRepo() {
        Long updatedUserId = 0L;
        User updatedUser = User.builder()
                .id(updatedUserId)
                .name("oldName")
                .email("oldEmail@email.ru")
                .build();
        UserDto updatedUserDto = UserDto.builder()
                .name("name")
                .email("email@email.ru")
                .build();
        User expectedUser = User.builder()
                .id(updatedUserId)
                .name("name")
                .email("email@email.ru")
                .build();
        when(userRepository.findById(updatedUserId))
                .thenReturn(Optional.of(getValidUser()));
        when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(getValidUser());

        userService.update(updatedUserId, updatedUserDto);
        verify(userRepository).save(userArgumentCaptor.capture());

        assertEquals(expectedUser, userArgumentCaptor.getValue(),
                "Invalid mapping User Dto -> User when update"
        );

    }

    @Test
    void update_whenSaved_thenMappedToUserDto() {
        Long updatedUserId = 0L;
        User updatedUser = User.builder()
                .id(updatedUserId)
                .name("oldName")
                .email("oldEmail@email.ru")
                .build();
        UserDto updatedUserDto = UserDto.builder()
                .name("name")
                .email("email@email.ru")
                .build();
        User expectedUser = User.builder()
                .id(updatedUserId)
                .name("name")
                .email("email@email.ru")
                .build();
        when(userRepository.findById(updatedUserId))
                .thenReturn(Optional.of(updatedUser));
        when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(getValidUser());

        userService.update(updatedUserId, updatedUserDto);
        verify(userRepository).save(userArgumentCaptor.capture());

        assertEquals(expectedUser, userArgumentCaptor.getValue(),
                "Invalid mapping User -> User Dto when update"
        );
    }

    @Test
    void getById() {
        Long userId = 0L;
        User foundUser = User.builder()
                .id(userId)
                .name("name")
                .email("email@email.ru")
                .build();
        UserDto expectedUserDto = UserDto.builder()
                .id(userId)
                .name("name")
                .email("email@email.ru")
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(foundUser));

        UserDto actualUserDto = userService.getById(userId);

        assertEquals(expectedUserDto, actualUserDto,
                "Invalid mapping User -> User Dto when getById"
        );
    }

    @Test
    void getAll() {
        List<User> foundUsers = List.of(
                User.builder()
                        .id(0L)
                        .name("user_1")
                        .email("email_1@email.ru")
                        .build(),
                User.builder()
                        .id(1L)
                        .name("user_2")
                        .email("email_2@email.ru")
                        .build()
        );
        List<UserDto> expectedUserDtos = List.of(
                UserDto.builder()
                        .id(0L)
                        .name("user_1")
                        .email("email_1@email.ru")
                        .build(),
                UserDto.builder()
                        .id(1L)
                        .name("user_2")
                        .email("email_2@email.ru")
                        .build()
        );
        when(userRepository.findAll())
                .thenReturn(foundUsers);

        List<UserDto> actualUserDtos = userService.getAll();

        assertThat(actualUserDtos, is(expectedUserDtos));
    }

    private User getValidUser() {
        return User.builder()
                .id(0L)
                .name("name")
                .email("email")
                .build();
    }

    private UserCreateDto getValidCreateDto() {
        return UserCreateDto.builder()
                .name("name")
                .email("email")
                .build();
    }
}