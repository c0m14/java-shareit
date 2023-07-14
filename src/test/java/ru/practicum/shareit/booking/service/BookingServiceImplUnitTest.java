package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidParamException;
import ru.practicum.shareit.exception.NotExistsException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ItemMapper itemMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;
    @Captor
    private ArgumentCaptor<PageRequest> pageRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<BookingState> bookingStateArgumentCaptor;

    @Test
    void add_whenUserNotFound_thenNotExistsExceptionThrown() {
        Long bookerId = 0L;
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder().build();
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> bookingService.add(bookerId, bookingCreationDto),
                "User not found but no NotExistsException thrown");
    }

    @Test
    void add_whenItemNotFound_thenNotExistsExceptionThrown() {
        Long bookerId = 0L;
        Long itemId = 0L;
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(itemId)
                .build();
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(getValidUser(bookerId)));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> bookingService.add(bookerId, bookingCreationDto),
                "Item not found but no NotExistsException thrown");
    }

    @Test
    void add_whenStartIsAfterEnd_thenInvalidParamException() {
        Long bookerId = 0L;
        Long itemId = 0L;
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2))
                .build();
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(getValidUser(bookerId)));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(getValidItem(itemId)));

        assertThrows(InvalidParamException.class,
                () -> bookingService.add(bookerId, bookingCreationDto),
                "Start is after End not found but no InvalidParamException thrown");
    }

    @Test
    void add_whenStartIsEqualEnd_thenInvalidParamException() {
        Long bookerId = 0L;
        Long itemId = 0L;
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.of(2023, 7, 20, 0, 0))
                .end(LocalDateTime.of(2023, 7, 20, 0, 0))
                .build();
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(getValidUser(bookerId)));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(getValidItem(itemId)));

        assertThrows(InvalidParamException.class,
                () -> bookingService.add(bookerId, bookingCreationDto),
                "Start is equal End not found but no InvalidParamException thrown");
    }

    @Test
    void add_whenItemNotAvailable_thenInvalidParamException() {
        Long bookerId = 0L;
        Long itemId = 0L;
        Item item = getValidItem(itemId);
        item.setAvailable(false);
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2))
                .build();
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(getValidUser(bookerId)));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        assertThrows(InvalidParamException.class,
                () -> bookingService.add(bookerId, bookingCreationDto),
                "Item not available but no InvalidParamException thrown");
    }

    @Test
    void add_whenRequestFromItemOwner_thenInvalidParamException() {
        Long bookerId = 0L;
        User booker = getValidUser(bookerId);
        Long itemId = 0L;
        Item item = getValidItem(itemId);
        item.setOwner(booker);
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2))
                .build();
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        assertThrows(InvalidParamException.class,
                () -> bookingService.add(bookerId, bookingCreationDto),
                "Request is from item owner but no InvalidParamException thrown");
    }

    @Test
    void changeStatus_whenBookingNotFound_thenNotExistsExceptionThrown() {
        Long bookingId = 0L;
        Long bookingOwnerId = 1L;
        boolean approved = true;
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> bookingService.changeStatus(bookingOwnerId, bookingId, approved),
                "Booking not found, but no NotExistsException thrown");
    }

    @Test
    void changeStatus_whenRequestedUserNotItemOwner_thenNotExistsExceptionThrown() {
        Long bookingId = 0L;
        Long bookingOwnerId = 1L;
        Long requestedUserId = 2L;
        Long itemId = 3L;
        User owner = getValidUser(bookingOwnerId);
        Item item = getValidItem(itemId);
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(owner)
                .state(BookingState.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2))
                .build();
        boolean approved = true;
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        assertThrows(NotExistsException.class,
                () -> bookingService.changeStatus(requestedUserId, bookingId, approved),
                "User from request is not booking owner, but no NotExistsException thrown");
    }

    @Test
    void changeStatus_whenApprovedIsTrue_thenStatusChangedToApproved() {
        Long bookingId = 0L;
        Long bookingOwnerId = 1L;
        Long itemId = 3L;
        User owner = getValidUser(bookingOwnerId);
        Item item = getValidItem(itemId);
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(owner)
                .state(BookingState.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2))
                .build();
        boolean approved = true;
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        bookingService.changeStatus(bookingOwnerId, bookingId, approved);

        verify(bookingRepository, times(1))
                .save(bookingArgumentCaptor.capture());

        assertEquals(BookingState.APPROVED, bookingArgumentCaptor.getValue().getState(),
                "Booking with invalid status passed to repository when chaneStatus to approved");
    }

    @Test
    void changeStatus_whenApprovedIsFalse_thenStatusChangedToRejected() {
        Long bookingId = 0L;
        Long bookingOwnerId = 1L;
        Long itemId = 3L;
        User owner = getValidUser(bookingOwnerId);
        Item item = getValidItem(itemId);
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(owner)
                .state(BookingState.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2))
                .build();
        boolean approved = false;
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        bookingService.changeStatus(bookingOwnerId, bookingId, approved);

        verify(bookingRepository, times(1))
                .save(bookingArgumentCaptor.capture());

        assertEquals(BookingState.REJECTED, bookingArgumentCaptor.getValue().getState(),
                "Booking with invalid status passed to repository when chaneStatus to approved");
    }

    @Test
    void changeStatus_whenBookingStateIsApproved_thenInvalidParamExceptionThrown() {
        Long bookingId = 0L;
        Long bookingOwnerId = 1L;
        Long itemId = 3L;
        User owner = getValidUser(bookingOwnerId);
        Item item = getValidItem(itemId);
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(owner)
                .state(BookingState.APPROVED)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2))
                .build();
        boolean approved = false;
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        assertThrows(InvalidParamException.class,
                () -> bookingService.changeStatus(bookingOwnerId, bookingId, approved),
                "Booking was unacceptable, but no InvalidParamException thrown");
    }

    @Test
    void changeStatus_whenBookingStateIsRejected_thenInvalidParamExceptionThrown() {
        Long bookingId = 0L;
        Long bookingOwnerId = 1L;
        Long itemId = 3L;
        User owner = getValidUser(bookingOwnerId);
        Item item = getValidItem(itemId);
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(owner)
                .state(BookingState.REJECTED)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2))
                .build();
        boolean approved = false;
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        assertThrows(InvalidParamException.class,
                () -> bookingService.changeStatus(bookingOwnerId, bookingId, approved),
                "Booking was unacceptable, but no InvalidParamException thrown");
    }

    @Test
    void changeStatus_whenBookingStateIsCancelled_thenInvalidParamExceptionThrown() {
        Long bookingId = 0L;
        Long bookingOwnerId = 1L;
        Long itemId = 3L;
        User owner = getValidUser(bookingOwnerId);
        Item item = getValidItem(itemId);
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(owner)
                .state(BookingState.CANCELLED)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2))
                .build();
        boolean approved = false;
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        assertThrows(InvalidParamException.class,
                () -> bookingService.changeStatus(bookingOwnerId, bookingId, approved),
                "Booking was unacceptable, but no InvalidParamException thrown");
    }

    @Test
    void getById_whenBookingNotFound_thenNotExistsExceptionThrown() {
        Long bookingId = 0L;
        Long requestedUserId = 1L;
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> bookingService.getById(requestedUserId, bookingId),
                "Booking not found but no NotExistsException thrown");
    }

    @Test
    void getById_whenRequestNotFromItemOrBookingOwner_thenNotExistsExceptionThrown() {
        Long bookingId = 0L;
        Long requestedUserId = 1L;
        Long itemId = 2L;
        Long itemOwnerId = 3L;
        Long bookingOwnerId = 4L;
        Item item = getValidItem(itemId);
        User bookingOwner = getValidUser(bookingOwnerId);
        User itemOwner = getValidUser(itemOwnerId);
        item.setOwner(itemOwner);
        Booking requestedBooking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(bookingOwner)
                .build();
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(requestedBooking));

        assertThrows(NotExistsException.class,
                () -> bookingService.getById(requestedUserId, bookingId),
                "User from request not item or booking owner but no NotExistsException thrown");
    }

    @Test
    void getById_whenRequestFromItemOwner_thenBookingReturned() {
        Long bookingId = 0L;
        Long itemId = 2L;
        Long itemOwnerId = 3L;
        Long bookingOwnerId = 4L;
        Item item = getValidItem(itemId);
        User bookingOwner = getValidUser(bookingOwnerId);
        User itemOwner = getValidUser(itemOwnerId);
        item.setOwner(itemOwner);
        Booking requestedBooking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(bookingOwner)
                .build();
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(requestedBooking));

        bookingService.getById(itemOwnerId, bookingId);

        verify(bookingMapper, times(1)).mapToDto(
                bookingArgumentCaptor.capture(),
                any(),
                any());
        assertEquals(bookingArgumentCaptor.getValue(), requestedBooking,
                "User is valid, but booking not returned");
    }

    @Test
    void getById_whenRequestFromBookingOwner_thenBookingPassedToMapper() {
        Long bookingId = 0L;
        Long itemId = 2L;
        Long itemOwnerId = 3L;
        Long bookingOwnerId = 4L;
        Item item = getValidItem(itemId);
        User bookingOwner = getValidUser(bookingOwnerId);
        User itemOwner = getValidUser(itemOwnerId);
        item.setOwner(itemOwner);
        Booking requestedBooking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(bookingOwner)
                .build();
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(requestedBooking));

        bookingService.getById(bookingOwnerId, bookingId);

        verify(bookingMapper, times(1)).mapToDto(
                bookingArgumentCaptor.capture(),
                any(),
                any());
        assertEquals(bookingArgumentCaptor.getValue(), requestedBooking,
                "User is valid, but booking not returned");
    }

    @Test
    void getByStateBooker_whenInvalidState_thenInvalidParamExceptionThrown() {
        Long bookerId = 0L;
        String state = "SomeText";
        int from = 0;
        int size = 0;

        assertThrows(InvalidParamException.class,
                () -> bookingService.getByStateBooker(bookerId, state, from, size),
                "Invalid state but no InvalidParamException thrown");
    }

    @Test
    void getByStateBooker_whenBookerNotFound_thenNotExistsExceptionThrown() {
        Long bookerId = 0L;
        String state = "all";
        int from = 0;
        int size = 0;
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> bookingService.getByStateBooker(bookerId, state, from, size),
                "User not found but no NotExistsException thrown");
    }

    @Test
    void getByStateBooker_whenFromIsZero_thenPageIsZero() {
        Long bookerId = 0L;
        String state = "all";
        int from = 0;
        int size = 20;
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(getValidUser(bookerId)));

        try {
            bookingService.getByStateBooker(bookerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository).findByBookerId(
                pageRequestArgumentCaptor.capture(),
                anyLong());

        assertEquals(0, pageRequestArgumentCaptor.getValue().getPageNumber());
    }

    @Test
    void getByStateBooker_thenFromLessThanSize_thenPageIsZero() {
        Long bookerId = 0L;
        String state = "all";
        int from = 5;
        int size = 20;
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(getValidUser(bookerId)));

        try {
            bookingService.getByStateBooker(bookerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository).findByBookerId(
                pageRequestArgumentCaptor.capture(),
                anyLong());

        assertEquals(0, pageRequestArgumentCaptor.getValue().getPageNumber());
    }

    @Test
    void getByStateBooker_whenStateIsAll_thenFindByBookerIdCalled() {
        Long bookerId = 0L;
        String state = "all";
        int from = 5;
        int size = 3;
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(getValidUser(bookerId)));

        try {
            bookingService.getByStateBooker(bookerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository, times(1)).findByBookerId(
                any(), anyLong());
        verify(bookingRepository, never()).findByBookerIdAndEndIsAfterAndStartIsBefore(
                any(), any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndEndIsBefore(
                any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndStartIsAfter(
                any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndState(
                any(), any(), any());
    }

    @Test
    void getByStateBooker_whenStateIsCurrent_thenFindByBookerIdAndEndIsAfterAndStartIsBeforeCalled() {
        Long bookerId = 0L;
        String state = "current";
        int from = 5;
        int size = 3;
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(getValidUser(bookerId)));

        try {
            bookingService.getByStateBooker(bookerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository, never()).findByBookerId(
                any(), anyLong());
        verify(bookingRepository, times(1)).findByBookerIdAndEndIsAfterAndStartIsBefore(
                any(), any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndEndIsBefore(
                any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndStartIsAfter(
                any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndState(
                any(), any(), any());
    }

    @Test
    void getByStateBooker_whenStateIsPast_thenFindByBookerIdAndEndIsBeforeCalled() {
        Long bookerId = 0L;
        String state = "past";
        int from = 5;
        int size = 3;
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(getValidUser(bookerId)));

        try {
            bookingService.getByStateBooker(bookerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository, never()).findByBookerId(
                any(), anyLong());
        verify(bookingRepository, never()).findByBookerIdAndEndIsAfterAndStartIsBefore(
                any(), any(), any(), any());
        verify(bookingRepository, times(1)).findByBookerIdAndEndIsBefore(
                any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndStartIsAfter(
                any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndState(
                any(), any(), any());
    }

    @Test
    void getByStateBooker_whenStateIsFuture_thenFindByBookerIdAndStartIsAfterCalled() {
        Long bookerId = 0L;
        String state = "future";
        int from = 5;
        int size = 3;
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(getValidUser(bookerId)));

        try {
            bookingService.getByStateBooker(bookerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository, never()).findByBookerId(
                any(), anyLong());
        verify(bookingRepository, never()).findByBookerIdAndEndIsAfterAndStartIsBefore(
                any(), any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndEndIsBefore(
                any(), any(), any());
        verify(bookingRepository, times(1)).findByBookerIdAndStartIsAfter(
                any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndState(
                any(), any(), any());
    }

    @Test
    void getByStateBooker_whenStateIsWaiting_thenFindByBookerIdAndStateCalled() {
        Long bookerId = 0L;
        String state = "waiting";
        int from = 5;
        int size = 3;
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(getValidUser(bookerId)));

        try {
            bookingService.getByStateBooker(bookerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository, never()).findByBookerId(
                any(), anyLong());
        verify(bookingRepository, never()).findByBookerIdAndEndIsAfterAndStartIsBefore(
                any(), any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndEndIsBefore(
                any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndStartIsAfter(
                any(), any(), any());
        verify(bookingRepository, times(1)).findByBookerIdAndState(
                any(), any(), bookingStateArgumentCaptor.capture());

        assertEquals(BookingState.WAITING, bookingStateArgumentCaptor.getValue(),
                String.format("Method with wrong Booking state used, expected %s", BookingState.WAITING));
    }

    @Test
    void getByStateBooker_whenStateIsRejected_thenFindByBookerIdAndStateCalled() {
        Long bookerId = 0L;
        String state = "rejected";
        int from = 5;
        int size = 3;
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(getValidUser(bookerId)));

        try {
            bookingService.getByStateBooker(bookerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository, never()).findByBookerId(
                any(), anyLong());
        verify(bookingRepository, never()).findByBookerIdAndEndIsAfterAndStartIsBefore(
                any(), any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndEndIsBefore(
                any(), any(), any());
        verify(bookingRepository, never()).findByBookerIdAndStartIsAfter(
                any(), any(), any());
        verify(bookingRepository, times(1)).findByBookerIdAndState(
                any(), any(), bookingStateArgumentCaptor.capture());

        assertEquals(BookingState.REJECTED, bookingStateArgumentCaptor.getValue(),
                String.format("Method with wrong Booking state used, expected %s", BookingState.REJECTED));
    }

    @Test
    void getByStateOwner_whenInvalidState_thenInvalidParamExceptionThrown() {
        Long ownerId = 0L;
        String state = "SomeText";
        int from = 0;
        int size = 0;

        assertThrows(InvalidParamException.class,
                () -> bookingService.getByStateOwner(ownerId, state, from, size),
                "Invalid state but no InvalidParamException thrown");
    }

    @Test
    void getByStateOwner_whenOwnerNotFound_thenNotExistsExceptionThrown() {
        Long ownerId = 0L;
        String state = "all";
        int from = 0;
        int size = 0;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> bookingService.getByStateOwner(ownerId, state, from, size),
                "Owner not found but no NotExistsException thrown");
    }

    @Test
    void getByStateOwner_whenOwnerHasNoItems_thenNotExistsExceptionThrown() {
        Long ownerId = 0L;
        String state = "all";
        int from = 0;
        int size = 0;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(getValidUser(ownerId)));
        when(itemRepository.findByOwnerId(ownerId))
                .thenReturn(List.of());

        assertThrows(NotExistsException.class,
                () -> bookingService.getByStateOwner(ownerId, state, from, size),
                "Owner has no Items but no NotExistsException thrown");
    }

    @Test
    void getByStateOwner_whenFromIsZero_thenPageIsZero() {
        Long ownerId = 0L;
        String state = "all";
        int from = 0;
        int size = 20;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(getValidUser(ownerId)));
        when(itemRepository.findByOwnerId(ownerId))
                .thenReturn(List.of(getValidItem(0L)));

        try {
            bookingService.getByStateOwner(ownerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository).findByItemOwnerId(
                pageRequestArgumentCaptor.capture(),
                anyLong());

        assertEquals(0, pageRequestArgumentCaptor.getValue().getPageNumber());
    }

    @Test
    void getByStateOwner_thenFromLessThanSize_thenPageIsZero() {
        Long ownerId = 0L;
        String state = "all";
        int from = 5;
        int size = 20;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(getValidUser(ownerId)));
        when(itemRepository.findByOwnerId(ownerId))
                .thenReturn(List.of(getValidItem(0L)));

        try {
            bookingService.getByStateOwner(ownerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository).findByItemOwnerId(
                pageRequestArgumentCaptor.capture(),
                anyLong());

        assertEquals(0, pageRequestArgumentCaptor.getValue().getPageNumber());
    }

    @Test
    void getByStateOwner_whenStateIsAll_thenFindByItemOwnerIdCalled() {
        Long ownerId = 0L;
        String state = "all";
        int from = 5;
        int size = 3;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(getValidUser(ownerId)));
        when(itemRepository.findByOwnerId(ownerId))
                .thenReturn(List.of(getValidItem(0L)));

        try {
            bookingService.getByStateOwner(ownerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository, times(1)).findByItemOwnerId(
                any(), anyLong());
        verify(bookingRepository, never()).findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
                any(), any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndEndIsBefore(
                any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndStartIsAfter(
                any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndState(
                any(), any(), any());
    }

    @Test
    void getByStateOwner_whenStateIsCurrent_thenFindByItemOwnerIdAndEndIsAfterAndStartIsBeforeCalled() {
        Long ownerId = 0L;
        String state = "current";
        int from = 5;
        int size = 3;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(getValidUser(ownerId)));
        when(itemRepository.findByOwnerId(ownerId))
                .thenReturn(List.of(getValidItem(0L)));

        try {
            bookingService.getByStateOwner(ownerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository, never()).findByItemOwnerId(
                any(), anyLong());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
                any(), any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndEndIsBefore(
                any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndStartIsAfter(
                any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndState(
                any(), any(), any());
    }

    @Test
    void getByStateOwner_whenStateIsPast_thenFindByItemOwnerIdAndEndIsBeforeCalled() {
        Long ownerId = 0L;
        String state = "past";
        int from = 5;
        int size = 3;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(getValidUser(ownerId)));
        when(itemRepository.findByOwnerId(ownerId))
                .thenReturn(List.of(getValidItem(0L)));

        try {
            bookingService.getByStateOwner(ownerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository, never()).findByItemOwnerId(
                any(), anyLong());
        verify(bookingRepository, never()).findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
                any(), any(), any(), any());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndEndIsBefore(
                any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndStartIsAfter(
                any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndState(
                any(), any(), any());
    }

    @Test
    void getByStateOwner_whenStateIsFuture_thenFindByItemOwnerIdAndStartIsAfterCalled() {
        Long ownerId = 0L;
        String state = "future";
        int from = 5;
        int size = 3;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(getValidUser(ownerId)));
        when(itemRepository.findByOwnerId(ownerId))
                .thenReturn(List.of(getValidItem(0L)));

        try {
            bookingService.getByStateOwner(ownerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository, never()).findByItemOwnerId(
                any(), anyLong());
        verify(bookingRepository, never()).findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
                any(), any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndEndIsBefore(
                any(), any(), any());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStartIsAfter(
                any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndState(
                any(), any(), any());
    }

    @Test
    void getByStateOwner_whenStateIsWaiting_thenFindByItemOwnerIdAndStateCalled() {
        Long ownerId = 0L;
        String state = "waiting";
        int from = 5;
        int size = 3;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(getValidUser(ownerId)));
        when(itemRepository.findByOwnerId(ownerId))
                .thenReturn(List.of(getValidItem(0L)));

        try {
            bookingService.getByStateOwner(ownerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository, never()).findByItemOwnerId(
                any(), anyLong());
        verify(bookingRepository, never()).findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
                any(), any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndEndIsBefore(
                any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndStartIsAfter(
                any(), any(), any());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndState(
                any(), any(), bookingStateArgumentCaptor.capture());

        assertEquals(BookingState.WAITING, bookingStateArgumentCaptor.getValue(),
                String.format("Method with wrong Booking state used, expected %s", BookingState.WAITING));
    }

    @Test
    void getByStateOwner_whenStateIsRejected_thenFindByItemOwnerIdAndStateCalled() {
        Long ownerId = 0L;
        String state = "rejected";
        int from = 5;
        int size = 3;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(getValidUser(ownerId)));
        when(itemRepository.findByOwnerId(ownerId))
                .thenReturn(List.of(getValidItem(0L)));

        try {
            bookingService.getByStateOwner(ownerId, state, from, size);
        } catch (NullPointerException e) {
            //capture value before end of method
        }

        verify(bookingRepository, never()).findByItemOwnerId(
                any(), anyLong());
        verify(bookingRepository, never()).findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
                any(), any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndEndIsBefore(
                any(), any(), any());
        verify(bookingRepository, never()).findByItemOwnerIdAndStartIsAfter(
                any(), any(), any());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndState(
                any(), any(), bookingStateArgumentCaptor.capture());

        assertEquals(BookingState.REJECTED, bookingStateArgumentCaptor.getValue(),
                String.format("Method with wrong Booking state used, expected %s", BookingState.REJECTED));
    }

    private User getValidUser(Long id) {
        return User.builder()
                .id(id)
                .name("userName")
                .email("email@email.ru")
                .build();
    }

    private Item getValidItem(Long id) {
        return Item.builder()
                .id(id)
                .name("name")
                .description("desc")
                .available(true)
                .owner(getValidUser(1L))
                .build();
    }
}