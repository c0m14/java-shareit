package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserServiceImplIntegrationTests {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void add_whenInvoked_thenSavedProperlyInDB() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("name")
                .email("email")
                .build();

        Long savedUserId = userService.add(userCreateDto).getId();

        User savedUser = userRepository.findById(savedUserId).get();
        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getName(), equalTo(userCreateDto.getName()));
        assertThat(savedUser.getEmail(), equalTo(userCreateDto.getEmail()));
    }

    @Test
    void update_whenInvoked_thenUpdatedProperlyInDB() {
        Long savedUserId = userRepository.save(User.builder()
                .name("oldName")
                .email("oldEmail")
                .build()).getId();
        UserDto updateUserDto = UserDto.builder()
                .id(savedUserId)
                .name("newName")
                .email("newEmail")
                .build();

        userService.update(savedUserId, updateUserDto);

        User updatedUser = userRepository.findById(savedUserId).get();
        assertThat(updatedUser.getName(), equalTo(updateUserDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updateUserDto.getEmail()));
    }

    @Test
    void getAll_whenNoUsers_thenEmptyListReturned() {
        userRepository.deleteAll();
        List<UserDto> foundUsers = userService.getAll();

        assertThat(foundUsers, empty());
    }

    @Test
    void getAll_whenUsersFound_thenListWithValidUsersDtoReturned() {
        userRepository.deleteAll();
        User user1 = User.builder()
                .name("user1")
                .email("user1@email.ru")
                .build();
        userRepository.save(user1);
        User user2 = User.builder()
                .name("user2")
                .email("user2@email.ru")
                .build();
        userRepository.save(user2);
        List<UserDto> foundUsers = userService.getAll();

        assertThat(foundUsers, hasSize(2));
        assertThat(foundUsers.get(0).getName(), equalTo(user1.getName()));
        assertThat(foundUsers.get(0).getEmail(), equalTo(user1.getEmail()));
        assertThat(foundUsers.get(1).getName(), equalTo(user2.getName()));
        assertThat(foundUsers.get(1).getEmail(), equalTo(user2.getEmail()));
    }

    @Test
    void delete_whenInvoked_thenUserDeleteFromDB() {
        User user1 = User.builder()
                .name("user1")
                .email("user1@email.ru")
                .build();
        Long savedUserId = userRepository.save(user1).getId();

        userService.delete(savedUserId);

        assertTrue(userRepository.findById(savedUserId).isEmpty());
    }


}