package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotExistsException;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private RequestServiceImpl requestService;
    @Captor
    private ArgumentCaptor<PageRequest> pageRequestArgumentCaptor;

    @Test
    void add_thenOwnerNotFound_thenNotExistsExceptionThrown() {
        Long ownerId = 0L;
        CreationRequestDto creationRequestDto = new CreationRequestDto();
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> requestService.add(ownerId, creationRequestDto),
                "Owner not found, but no NotExistsException thrown");
    }

    @Test
    void getAllUserItemRequests_whenOwnerNotFound_thenNotExistsExceptionThrown() {
        Long ownerId = 0L;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> requestService.getAllUserItemRequests(ownerId),
                "Owner not found, but no NotExistsException thrown");
    }

    @Test
    void getAll_whenFromIsZero_thenPageIsZero() {
        Long userId = 0L;
        int from = 0;
        int size = 1;

        try {
            requestService.getAllOtherUsersRequests(userId, from, size);
        } catch (Exception ex) {
            //capture value before end of method
        }

        verify(requestRepository, times(1))
                .findAll(pageRequestArgumentCaptor.capture());
        assertThat(pageRequestArgumentCaptor.getValue().getPageNumber(), equalTo(0));
    }

    @Test
    void getAll_whenFromLessThanSize_thenPageIsZero() {
        Long userId = 0L;
        int from = 2;
        int size = 5;

        try {
            requestService.getAllOtherUsersRequests(userId, from, size);
        } catch (Exception ex) {
            //capture value before end of method
        }

        verify(requestRepository, times(1))
                .findAll(pageRequestArgumentCaptor.capture());
        assertThat(pageRequestArgumentCaptor.getValue().getPageNumber(), equalTo(0));
    }

    @Test
    void getAll_whenFromBiggerThanSize_thenPageIsFromDividedBySize() {
        Long userId = 0L;
        int from = 5;
        int size = 2;

        try {
            requestService.getAllOtherUsersRequests(userId, from, size);
        } catch (Exception ex) {
            //capture value before end of method
        }

        verify(requestRepository, times(1))
                .findAll(pageRequestArgumentCaptor.capture());
        assertThat(pageRequestArgumentCaptor.getValue().getPageNumber(), equalTo(from / size));
    }

    @Test
    void getAll_whenInvoked_thenSortIsByCreatedDesc() {
        Long userId = 0L;
        int from = 5;
        int size = 2;

        try {
            requestService.getAllOtherUsersRequests(userId, from, size);
        } catch (Exception ex) {
            //capture value before end of method
        }

        verify(requestRepository, times(1))
                .findAll(pageRequestArgumentCaptor.capture());
        assertThat(pageRequestArgumentCaptor.getValue().getSort(),
                equalTo(Sort.by(Sort.Direction.DESC, "created")));
    }

    @Test
    void getById_thenUserNotFound_thenNotExistsExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> requestService.getAllUserItemRequests(userId),
                "User not found, but no NotExistsException thrown");
    }

    @Test
    void getById_thenRequestNotFound_thenNotExistsExceptionThrown() {
        Long userId = 0L;
        Long requestId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(getValidUser(userId)));
        when(requestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> requestService.getById(userId, requestId),
                "User not found, but no NotExistsException thrown");
    }

    private User getValidUser(Long id) {
        return User.builder()
                .id(id)
                .name("userName")
                .email("email@email.ru")
                .build();
    }
}