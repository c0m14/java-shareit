package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStateSearchDto;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDto add(Long bookerId, BookingCreationDto bookingCreationDto) {
        User booker = getUserById(bookerId);
        Item item = getItemById(bookingCreationDto.getItemId());

        validateDates(bookingCreationDto);
        validateIfAvailable(item);
        validateIfOwner(bookerId, item.getOwner().getId());

        Booking booking = bookingMapper.mapToEntity(bookingCreationDto, booker, item);
        booking.setState(BookingState.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.mapToDto(
                savedBooking,
                userMapper.mapToBookingDto(savedBooking.getBooker()),
                itemMapper.mapToBookingDto(savedBooking.getItem())
        );
    }

    @Override
    @Transactional
    public BookingDto changeStatus(Long itemOwnerId, Long bookingId, boolean approved) {
        Booking booking = getBookingById(bookingId);
        Item item = booking.getItem();

        validateIfItemOwner(item, itemOwnerId);

        if (booking.getState().equals(BookingState.WAITING)) {
            if (approved) {
                booking.setState(BookingState.APPROVED);
            } else {
                booking.setState(BookingState.REJECTED);
            }
        } else {
            throw new InvalidParamException(
                    "State",
                    String.format("Booking state is %s and not valid for approval", booking.getState())
            );
        }

        return bookingMapper.mapToDto(
                bookingRepository.save(booking),
                userMapper.mapToBookingDto(booking.getBooker()),
                itemMapper.mapToBookingDto(booking.getItem())
        );
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking requestedBooking = getBookingById(bookingId);
        Item item = requestedBooking.getItem();

        validateUserIfItemOwnerOrBooker(item, requestedBooking, userId);

        return bookingMapper.mapToDto(
                requestedBooking,
                userMapper.mapToBookingDto(requestedBooking.getBooker()),
                itemMapper.mapToBookingDto(requestedBooking.getItem())
        );

    }

    @Override
    public List<BookingDto> getByStateBooker(Long bookerId, String state, int from, int size) {
        BookingStateSearchDto bookingState = getBookingState(state);
        validateIfUserExist(bookerId);
        Page<Booking> requestedBookings = null;
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sortByStartDesc);


        switch (bookingState) {
            case ALL:
                requestedBookings = bookingRepository.findByBookerId(pageRequest, bookerId);
                break;
            case CURRENT:
                requestedBookings = bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(
                        pageRequest,
                        bookerId,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );
                break;
            case PAST:
                requestedBookings = bookingRepository.findByBookerIdAndEndIsBefore(
                        pageRequest,
                        bookerId,
                        LocalDateTime.now()
                );
                break;
            case FUTURE:
                requestedBookings = bookingRepository.findByBookerIdAndStartIsAfter(
                        pageRequest,
                        bookerId,
                        LocalDateTime.now()
                );
                break;
            case WAITING:
                requestedBookings = bookingRepository.findByBookerIdAndState(
                        pageRequest,
                        bookerId,
                        BookingState.WAITING
                );
                break;
            case REJECTED:
                requestedBookings = bookingRepository.findByBookerIdAndState(
                        pageRequest,
                        bookerId,
                        BookingState.REJECTED
                );
                break;
        }

        return bookingMapper.mapToListDto(requestedBookings.toList());
    }

    @Override
    public List<BookingDto> getByStateOwner(Long ownerId, String state, int from, int size) {
        BookingStateSearchDto bookingState = getBookingState(state);
        validateIfUserExist(ownerId);
        validateIfUserHasItems(ownerId);
        Page<Booking> requestedBookings = null;
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sortByStartDesc);

        switch (bookingState) {
            case ALL:
                requestedBookings = bookingRepository.findByItemOwnerId(pageRequest, ownerId);
                break;
            case CURRENT:
                requestedBookings = bookingRepository.findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
                        pageRequest,
                        ownerId,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );
                break;
            case PAST:
                requestedBookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(
                        pageRequest,
                        ownerId,
                        LocalDateTime.now()
                );
                break;
            case FUTURE:
                requestedBookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(
                        pageRequest,
                        ownerId,
                        LocalDateTime.now()
                );
                break;
            case WAITING:
                requestedBookings = bookingRepository.findByItemOwnerIdAndState(
                        pageRequest,
                        ownerId,
                        BookingState.WAITING
                );
                break;
            case REJECTED:
                requestedBookings = bookingRepository.findByItemOwnerIdAndState(
                        pageRequest,
                        ownerId,
                        BookingState.REJECTED
                );
                break;
        }

        return bookingMapper.mapToListDto(requestedBookings.toList());
    }

    private void validateIfUserExist(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotExistsException(
                        "User",
                        String.format("User with id %d does not exist", userId)
                )
        );
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotExistsException(
                        "User",
                        String.format("User with id %d does not exist", userId)
                )
        );
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotExistsException(
                        "Item",
                        String.format("Item with id %d does not exist", itemId)
                )
        );
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotExistsException(
                        "Booking",
                        String.format("Booking with id %d does not exist", bookingId)
                ));
    }

    private void validateIfOwner(Long userId, Long ownerId) {
        if (Objects.equals(userId, ownerId)) {
            throw new NotExistsException(
                    "Item",
                    "Owner can't add booking for his own item"
            );
        }
    }

    private BookingStateSearchDto getBookingState(String stringState) {
        try {
            return BookingStateSearchDto.valueOf(stringState.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidParamException(
                    "State",
                    String.format("Unknown state: %s", stringState)
            );
        }
    }

    private void validateIfUserHasItems(Long userId) {
        if (itemRepository.findByOwnerId(userId).isEmpty()) {
            throw new NotExistsException(
                    "Items",
                    String.format("There is no items for user with id %d", userId)
            );
        }
    }

    private void validateDates(BookingCreationDto booking) {
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new InvalidParamException(
                    "Start",
                    String.format("Start: %s is before end: %s",
                            booking.getStart(),
                            booking.getEnd())
            );
        }
        if (booking.getStart().equals(booking.getEnd())) {
            throw new InvalidParamException(
                    "Start",
                    String.format("Start: %s is equals end: %s",
                            booking.getStart(),
                            booking.getEnd())
            );
        }
    }

    private void validateIfAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new InvalidParamException(
                    "Item",
                    String.format("Item with id %d is not available for booking", item.getId())
            );
        }
    }

    private void validateIfItemOwner(Item item, Long requestUserId) {
        if (!Objects.equals(item.getOwner().getId(), requestUserId)) {
            throw new NotExistsException(
                    "User Id",
                    String.format("User with id %d is not item owner", requestUserId)
            );
        }
    }

    private void validateUserIfItemOwnerOrBooker(Item item, Booking booking, Long requestUserId) {
        if ((!Objects.equals(item.getOwner().getId(), requestUserId)) &&
                (!Objects.equals(booking.getBooker().getId(), requestUserId))
        ) {
            throw new NotExistsException(
                    "User Id",
                    String.format("User with id %d is not item owner or booker", requestUserId)
            );
        }
    }


}
