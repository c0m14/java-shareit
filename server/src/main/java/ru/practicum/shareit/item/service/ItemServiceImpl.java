package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidParamException;
import ru.practicum.shareit.exception.NotExistsException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final Sort sortByStartAsc = Sort.by(Sort.Direction.ASC, "start");
    private final Sort sortByEndDesc = Sort.by(Sort.Direction.DESC, "end");

    @Override
    @Transactional
    public ItemDto add(Long userId, ItemCreateDto itemCreateDto) {
        User owner = getUserById(userId);
        Item itemToSave;
        boolean hasRequest = itemCreateDto.getRequestId() != null;

        if (hasRequest) {
            Request itemRequest = getRequestById(itemCreateDto.getRequestId());
            itemToSave = itemMapper.mapToItem(itemCreateDto, owner, itemRequest);
        } else {
            itemToSave = itemMapper.mapToItem(itemCreateDto, owner);
        }

        Item savedItem = itemRepository.save(itemToSave);

        if (hasRequest) {
            return itemMapper.mapToDto(savedItem, itemCreateDto.getRequestId());
        } else {
            return itemMapper.mapToDto(savedItem);
        }
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item updatedItem = getItemById(itemId);
        validateIfUserIsOwner(updatedItem, userId);

        updateFields(updatedItem, itemDto);

        Item savedItem = itemRepository.save(updatedItem);
        if (savedItem.getRequest() != null) {
            return itemMapper.mapToDto(savedItem, savedItem.getRequest().getRequestId());
        } else {
            return itemMapper.mapToDto(savedItem);
        }
    }

    @Override
    public ItemWithBookingsAndCommentsDto getById(Long userId, Long itemId) {
        validateIfUserExist(userId);
        Item requestedItem = getItemById(itemId);
        LastNextBookingDto last = null;
        LastNextBookingDto next = null;
        List<CommentDto> comments = getCommentsDtoForItem(itemId);

        if (Objects.equals(userId, requestedItem.getOwner().getId())) {
            last = findLastBooking(itemId);
            next = findNextBooking(itemId);
        }
        return itemMapper.mapToWithBookingsDto(requestedItem, last, next, comments);
    }

    @Override
    public List<ItemWithBookingsAndCommentsDto> getUserItems(Long userId, int from, int size) {
        validateIfUserExist(userId);
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        return itemRepository.findByOwnerId(pageRequest, userId).stream()
                .map(item -> {
                    LastNextBookingDto last = findLastBooking(item.getId());
                    LastNextBookingDto next = findNextBooking(item.getId());
                    List<CommentDto> comments = getCommentsDtoForItem(item.getId());

                    return itemMapper.mapToWithBookingsDto(item, last, next, comments);
                })
                .sorted(Comparator.comparingLong(ItemWithBookingsAndCommentsDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String requestedText, int from, int size) {
        if (requestedText.isBlank()) {
            return List.of();
        }
        validateIfUserExist(userId);
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        return itemRepository.searchByText(pageRequest, requestedText)
                .stream()
                .map(itemMapper::mapToDto)
                .collect(Collectors.toList()
                );
    }

    @Override
    @Transactional
    public CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto) {
        User author = getUserById(authorId);
        Item item = getItemById(itemId);
        validateIfUserHasBookingsForItem(authorId, itemId);

        Comment comment = commentMapper.mapToComment(commentDto, item, author);

        return commentMapper.mapToDto(
                commentRepository.save(comment)
        );
    }

    private List<CommentDto> getCommentsDtoForItem(Long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream()
                .filter(Objects::nonNull)
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private void validateIfUserHasBookingsForItem(Long userId, Long itemId) {
        List<Booking> bookings = bookingRepository.findByStateAndBookerIdAndItemIdAndEndIsBefore(
                BookingState.APPROVED,
                userId,
                itemId,
                LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new InvalidParamException(
                    "Booking",
                    String.format("There is no ended bookings for user id %d and itemId %d",
                            userId, itemId)
            );
        }
    }

    private LastNextBookingDto findLastBooking(Long itemId) {
        return bookingRepository.findTopByStateAndItemIdAndStartIsBefore(
                        BookingState.APPROVED,
                        itemId,
                        LocalDateTime.now(),
                        sortByEndDesc)
                .map(bookingMapper::mapToLastNextDto)
                .orElse(null);
    }

    private LastNextBookingDto findNextBooking(Long itemId) {
        return bookingRepository.findTopByStateAndItemIdAndStartAfter(
                        BookingState.APPROVED,
                        itemId,
                        LocalDateTime.now(),
                        sortByStartAsc)
                .map(bookingMapper::mapToLastNextDto)
                .orElse(null);
    }

    private void validateIfUserIsOwner(Item item, Long userId) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotExistsException(
                    "Item",
                    String.format("Item with id %d not found for user with id %d", item.getId(), userId)
            );
        }
    }

    private void validateIfUserExist(Long userId) {
        getUserById(userId);
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
                        String.format("Item with id %d not exist", itemId)
                )
        );
    }

    private void updateFields(Item updatedItem, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            Request newRequest = getRequestById(itemDto.getRequestId());
            updatedItem.setRequest(newRequest);
        } else {
            updatedItem.setRequest(null);
        }
    }

    private Request getRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new NotExistsException(
                        "Request",
                        String.format("Request with id %d not exist", requestId)
                )
        );
    }

}
