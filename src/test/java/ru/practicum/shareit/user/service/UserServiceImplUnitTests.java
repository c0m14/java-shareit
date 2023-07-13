package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.exception.DuplicateEntityException;
import ru.practicum.shareit.exception.NotExistsException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTests {

    private static User mockUser;
    private static UserCreateDto mockUserCreateDto;
    private static UserDto mockUserDto;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserMapper userMapper;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @BeforeAll
    static void setup() {
        mockUser = mock(User.class);
        mockUserCreateDto = mock(UserCreateDto.class);
        mockUserDto = mock(UserDto.class);
    }

    @Test
    void add_whenCreatedWithDuplicateEmail_thenDuplicateEntityExceptionThrown() {
        when(userMapper.mapToUser(mockUserCreateDto))
                .thenReturn(mockUser);
        when(userRepository.save(mockUser))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateEntityException.class,
                () -> userService.add(mockUserCreateDto),
                "DuplicateEntityException not thrown when user with duplicate email"
        );
    }

    @Test
    void update_whenOnlyNameUpdated_thenEmailIsNotUpdated() {
        Long userId = 1L;
        User savedUser = User.builder()
                .id(1L)
                .name("oldName")
                .email("oldEmail@test.ru")
                .build();
        UserDto userToUpdate = UserDto.builder()
                .id(1L)
                .name("newName")
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(savedUser));

        userService.update(userId, userToUpdate);

        verify(userRepository).save(userArgumentCaptor.capture());
        User updatedUser = userArgumentCaptor.getValue();

        assertEquals("newName", updatedUser.getName(), "Name not updated");
        assertEquals("oldEmail@test.ru", updatedUser.getEmail(), "Email updated, but should not");
    }

    @Test
    void update_whenOnlyEmailUpdated_thenNameIsNotUpdated() {
        Long userId = 1L;
        User savedUser = User.builder()
                .id(1L)
                .name("oldName")
                .email("oldEmail@test.ru")
                .build();
        UserDto userToUpdate = UserDto.builder()
                .id(1L)
                .email("newEmail@test.ru")
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(savedUser));

        userService.update(userId, userToUpdate);

        verify(userRepository).save(userArgumentCaptor.capture());
        User updatedUser = userArgumentCaptor.getValue();

        assertEquals("newEmail@test.ru", updatedUser.getEmail(), "Email not updated");
        assertEquals("oldName", updatedUser.getName(), "Name updated, but should not");
    }

    @Test
    void update_whenUserNotFound_thenNotExistsExceptionThrown() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(NotExistsException.class,
                () -> userService.update(anyLong(), mockUserDto),
                "NotExistsException not thrown when user not found");
    }

    @Test
    void getById_whenUserNotFound_thenNotExistsExceptionThrown() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(NotExistsException.class,
                () -> userService.getById(anyLong()),
                "NotExistsException not thrown when user not found");
    }

    @Test
    void getAll_whenUsersNotFound_thenEmptyListReturned() {
        when(userRepository.findAll())
                .thenReturn(List.of());

        List<UserDto> users = userService.getAll();

        assertTrue(users.isEmpty(),
                "Users not found, but list is not empty");
    }

    @Test
    void delete_whenDeletedNotExistUser_thenNotExistsExceptionThrown() {
        doThrow(EmptyResultDataAccessException.class)
                .when(userRepository).deleteById(anyLong());

        assertThrows(NotExistsException.class,
                () -> userService.delete(anyLong()),
                "NotExistsException not thrown when user not found");
    }
}