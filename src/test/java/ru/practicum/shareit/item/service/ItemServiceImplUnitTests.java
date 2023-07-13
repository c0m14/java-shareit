package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidParamException;
import ru.practicum.shareit.exception.NotExistsException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTests {

    private static ItemCreateDto mockItemCreateDto;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Captor
    private ArgumentCaptor<Request> requestArgumentCaptor;
    @Captor
    private ArgumentCaptor<ItemCreateDto> itemCreateDtoArgumentCaptor;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    private ArgumentCaptor<List<CommentDto>> commentsDtoListCaptor;
    @Captor
    private ArgumentCaptor<PageRequest> pageRequestArgumentCaptor;


    @BeforeAll
    static void setup() {
        mockItemCreateDto = mock(ItemCreateDto.class);
    }

    @Test
    void add_whenUserNotFound_thenNotExistsExceptionThrown() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> itemService.add(anyLong(), mockItemCreateDto)
        );

    }

    @Test
    void add_thenItemHasRequest_whenItemRequestPassedToMappers() {
        Long userId = 0L;
        Long requestId = 1L;
        ItemCreateDto addedItemDto = ItemCreateDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(requestId)
                .build();
        Request itemRequest = Request.builder()
                .requestId(requestId)
                .description("description")
                .owner(getValidUser(0L))
                .created(LocalDateTime.now())
                .build();
        Item someValidItem = getValidItem(1L);
        when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(getValidUser(0L)));
        when(itemMapper.mapToItem(any(ItemCreateDto.class), any(User.class), any(Request.class)))
                .thenReturn(someValidItem);
        when(itemRepository.save(someValidItem))
                .thenReturn(someValidItem);

        itemService.add(userId, addedItemDto);

        verify(itemMapper, times(1)).mapToItem(
                itemCreateDtoArgumentCaptor.capture(),
                userArgumentCaptor.capture(),
                requestArgumentCaptor.capture());
        assertEquals(addedItemDto, itemCreateDtoArgumentCaptor.getValue(),
                "Invalid ItemCreateDto passed to mapToItem");
        assertEquals(itemRequest, requestArgumentCaptor.getValue(),
                "Invalid Item Request passed to mapToItem");
        verify(itemMapper, times(1)).mapToDto(
                any(Item.class),
                anyLong());
    }

    @Test
    void add_thenItemHasRequest_whenMethodsInvokesInRightOrder() {
        Long userId = 0L;
        Long requestId = 1L;
        ItemCreateDto addedItemDto = ItemCreateDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(requestId)
                .build();
        Request itemRequest = Request.builder()
                .requestId(requestId)
                .description("description")
                .owner(getValidUser(0L))
                .created(LocalDateTime.now())
                .build();
        Item someValidItem = getValidItem(1L);
        when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(getValidUser(0L)));
        when(itemMapper.mapToItem(any(ItemCreateDto.class), any(User.class), any(Request.class)))
                .thenReturn(someValidItem);
        when(itemRepository.save(someValidItem))
                .thenReturn(someValidItem);

        itemService.add(userId, addedItemDto);

        InOrder inOrder = inOrder(userRepository, requestRepository, itemMapper, itemRepository);
        inOrder.verify(userRepository).findById(anyLong());
        inOrder.verify(requestRepository).findById(anyLong());
        inOrder.verify(itemMapper).mapToItem(
                any(ItemCreateDto.class),
                any(User.class),
                any(Request.class));
        inOrder.verify(itemRepository).save(any(Item.class));
        inOrder.verify(itemMapper).mapToDto(any(Item.class), anyLong());
    }

    @Test
    void add_thenItemHasRequestButRequestNotFound_thenNotExistsExceptionThrown() {
        Long userId = 0L;
        ItemCreateDto addedItemDto = ItemCreateDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(userId)
                .build();
        when(requestRepository.findById(0L))
                .thenReturn(Optional.empty());
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(getValidUser(0L)));

        assertThrows(NotExistsException.class,
                () -> itemService.add(userId, addedItemDto),
                "Request not found, but no NotExistsException thrown"
        );
    }

    @Test
    void add_thenItemHasNotRequest_whenMethodsInvokesInRightOrder() {
        Long userId = 0L;
        ItemCreateDto addedItemDto = ItemCreateDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .build();
        Item someValidItem = getValidItem(1L);
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(getValidUser(0L)));
        when(itemMapper.mapToItem(any(ItemCreateDto.class), any(User.class)))
                .thenReturn(someValidItem);
        when(itemRepository.save(someValidItem))
                .thenReturn(someValidItem);

        itemService.add(userId, addedItemDto);

        InOrder inOrder = inOrder(userRepository, itemMapper, itemRepository);
        inOrder.verify(userRepository).findById(anyLong());
        inOrder.verify(itemMapper).mapToItem(
                any(ItemCreateDto.class),
                any(User.class));
        inOrder.verify(itemRepository).save(any(Item.class));
        inOrder.verify(itemMapper).mapToDto(any(Item.class));
    }

    @Test
    void add_thenItemHasNotRequest_whenMapperWithoutRequestUsed() {
        Long userId = 0L;
        ItemCreateDto addedItemDto = ItemCreateDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .build();
        Item someValidItem = getValidItem(1L);
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(getValidUser(0L)));
        when(itemMapper.mapToItem(any(ItemCreateDto.class), any(User.class)))
                .thenReturn(someValidItem);
        when(itemRepository.save(someValidItem))
                .thenReturn(someValidItem);

        itemService.add(userId, addedItemDto);

        verify(itemMapper, times(1)).mapToItem(
                itemCreateDtoArgumentCaptor.capture(),
                any(User.class));
        assertEquals(addedItemDto, itemCreateDtoArgumentCaptor.getValue(),
                "Invalid ItemCreateDto passed to mapToItem");
        verify(itemMapper, times(1)).mapToDto(
                any(Item.class));
    }

    @Test
    void update_whenUpdatedItemNotFound_thenNotNotExistsExceptionThrown() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> itemService.update(0L, 0L, getValidItemDto(0L)),
                "Item not found but no NotNotExistsException thrown"
        );
    }

    @Test
    void update_whenUpdatedRequestNotFromItemOwner_thenNotNotExistsExceptionThrown() {
        Long itemId = 0L;
        Long itemOwnerId = 0L;
        Long userIdFromRequest = 1L;
        Item savedItem = Item.builder()
                .id(itemId)
                .owner(getValidUser(itemOwnerId))
                .build();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(savedItem));

        assertThrows(NotExistsException.class,
                () -> itemService.update(userIdFromRequest, itemId, getValidItemDto(itemId)),
                "Item not found for user from request but no NotNotExistsException thrown"
        );
    }

    @Test
    void update_whenOnlyNameUpdated_thenOtherFieldsNotChanged() {
        Long itemId = 0L;
        User owner = getValidUser(0L);
        Request request = getValidRequest(0L);
        Item originalItem = Item.builder()
                .id(itemId)
                .name("oldName")
                .description("oldDescription")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        Long userId = originalItem.getOwner().getId();
        ItemDto updateDto = ItemDto.builder()
                .name("newName")
                .build();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(originalItem));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(originalItem);

        itemService.update(userId, itemId, updateDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item capturedItem = itemArgumentCaptor.getValue();
        assertEquals("newName", capturedItem.getName(),
                "Name not updated, but should");
        assertEquals("oldDescription", capturedItem.getDescription(),
                "Description updated, but should not");
        assertEquals(true, capturedItem.getAvailable(),
                "Available updated, but should not");
        assertEquals(owner, capturedItem.getOwner(),
                "Owner updated, but should not");
    }

    @Test
    void update_whenOnlyDescriptionUpdated_thenOtherFieldsNotChanged() {
        Long itemId = 0L;
        User owner = getValidUser(0L);
        Request request = getValidRequest(0L);
        Item originalItem = Item.builder()
                .id(itemId)
                .name("oldName")
                .description("oldDescription")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        Long userId = originalItem.getOwner().getId();
        ItemDto updateDto = ItemDto.builder()
                .description("newDescription")
                .build();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(originalItem));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(originalItem);

        itemService.update(userId, itemId, updateDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item capturedItem = itemArgumentCaptor.getValue();
        assertEquals("oldName", capturedItem.getName(),
                "Name updated, but should not");
        assertEquals("newDescription", capturedItem.getDescription(),
                "Description not updated, but should");
        assertEquals(true, capturedItem.getAvailable(),
                "Available updated, but should not");
        assertEquals(owner, capturedItem.getOwner(),
                "Owner updated, but should not");
    }

    @Test
    void update_whenOnlyAvailableUpdated_thenOtherFieldsNotChanged() {
        Long itemId = 0L;
        User owner = getValidUser(0L);
        Request request = getValidRequest(0L);
        Item originalItem = Item.builder()
                .id(itemId)
                .name("oldName")
                .description("oldDescription")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        Long userId = originalItem.getOwner().getId();
        ItemDto updateDto = ItemDto.builder()
                .available(false)
                .build();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(originalItem));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(originalItem);

        itemService.update(userId, itemId, updateDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item capturedItem = itemArgumentCaptor.getValue();
        assertEquals("oldName", capturedItem.getName(),
                "Name updated, but should not");
        assertEquals("oldDescription", capturedItem.getDescription(),
                "Description updated, but should not");
        assertEquals(false, capturedItem.getAvailable(),
                "Available not updated, but should");
        assertEquals(owner, capturedItem.getOwner(),
                "Owner updated, but should not");
    }

    @Test
    void update_whenOnlyRequestUpdated_thenOtherFieldsNotChanged() {
        Long itemId = 0L;
        User owner = getValidUser(0L);
        Request request = getValidRequest(0L);
        Request newRequest = getValidRequest(1L);
        Item originalItem = Item.builder()
                .id(itemId)
                .name("oldName")
                .description("oldDescription")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        Long userId = originalItem.getOwner().getId();
        ItemDto updateDto = ItemDto.builder()
                .requestId(newRequest.getRequestId())
                .build();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(originalItem));
        when(requestRepository.findById(newRequest.getRequestId()))
                .thenReturn(Optional.of(newRequest));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(originalItem);

        itemService.update(userId, itemId, updateDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item capturedItem = itemArgumentCaptor.getValue();
        assertEquals("oldName", capturedItem.getName(),
                "Name updated, but should not");
        assertEquals("oldDescription", capturedItem.getDescription(),
                "Description updated, but should not");
        assertEquals(true, capturedItem.getAvailable(),
                "Available updated, but should not");
        assertEquals(newRequest, capturedItem.getRequest(),
                "Request not updated, but should");
        assertEquals(owner, capturedItem.getOwner(),
                "Owner updated, but should not");
    }

    @Test
    void update_whenRequestUpdatedButNotFound_thenNotExistsExceptionThrown() {
        Long itemId = 0L;
        User owner = getValidUser(0L);
        Request request = getValidRequest(0L);
        Long invalidRequestId = 1L;
        Item originalItem = Item.builder()
                .id(itemId)
                .name("oldName")
                .description("oldDescription")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        Long userId = originalItem.getOwner().getId();
        ItemDto updateDto = ItemDto.builder()
                .requestId(invalidRequestId)
                .build();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(originalItem));
        when(requestRepository.findById(invalidRequestId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> itemService.update(userId, itemId, updateDto),
                "Updated request not found, but no NotExistsException thrown"
        );
    }

    @Test
    void update_whenInvoked_thenMethodsInvokedInRightOrder() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(getValidItem(0L)));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(getValidItem(0L));

        itemService.update(1L, 0L, getValidItemDto(0L));

        InOrder inOrder = inOrder(itemMapper, itemRepository);
        inOrder.verify(itemRepository).findById(anyLong());
        inOrder.verify(itemRepository).save(any(Item.class));
        inOrder.verify(itemMapper).mapToDto(any(Item.class), anyLong());
    }

    @Test
    void getById_whenUserNotFound_thenNotExistsExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> itemService.getById(userId, anyLong()),
                "User not found but no NotExistsException thrown"
        );
    }

    @Test
    void getById_whenItemNotFound_thenNotExistsExceptionThrown() {
        Long userId = 0L;
        Long itemId = 0L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(getValidUser(userId)));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> itemService.getById(userId, itemId),
                "Item not found but no NotExistsException thrown"
        );
    }

    @Test
    void getUserItems_whenUserNotFound_thenNotExistsExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> itemService.getUserItems(userId, 0, 1),
                "User not found, but no NotExistsException thrown"
        );
    }

    @Test
    void getUserItems_thenFromIsZero_thenPageIsZero() {
        Long userId = 0L;
        int from = 0;
        int size = 1;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(getValidUser(userId)));
        when(itemRepository.findByOwnerId(any(), any()))
                .thenReturn(Page.empty());

        itemService.getUserItems(userId, from, size);

        verify(itemRepository).findByOwnerId(
                pageRequestArgumentCaptor.capture(),
                anyLong()
        );
        assertEquals(0, pageRequestArgumentCaptor.getValue().getPageNumber());
    }

    @Test
    void getUserItems_thenFromLessThanSize_thenPageIsZero() {
        Long userId = 0L;
        int from = 3;
        int size = 5;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(getValidUser(userId)));
        when(itemRepository.findByOwnerId(any(), any()))
                .thenReturn(Page.empty());

        itemService.getUserItems(userId, from, size);

        verify(itemRepository).findByOwnerId(
                pageRequestArgumentCaptor.capture(),
                anyLong()
        );
        assertEquals(0, pageRequestArgumentCaptor.getValue().getPageNumber());
    }

    @Test
    void getUserItems_thenFromMoreThanSize_thenPageIsFromDivideBySize() {
        Long userId = 0L;
        int from = 5;
        int size = 3;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(getValidUser(userId)));
        when(itemRepository.findByOwnerId(any(), any()))
                .thenReturn(Page.empty());

        itemService.getUserItems(userId, from, size);

        verify(itemRepository).findByOwnerId(
                pageRequestArgumentCaptor.capture(),
                anyLong()
        );
        assertEquals(1, pageRequestArgumentCaptor.getValue().getPageNumber());
    }

    @Test
    void searchItems_whenSearchTextIsBlank_whenEmptyListReturned() {
        String searchText = " ";
        List<ItemDto> response = itemService.searchItems(0L, searchText, 0, 1);

        assertTrue(response.isEmpty());
    }

    @Test
    void searchItems_whenUserNotFound_thenNotExistsExceptionThrown() {
        String searchText = "text";
        Long userId = 0L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> itemService.searchItems(userId, searchText, 0, 1),
                "User not found but no NotExistsException thrown"
        );
    }

    @Test
    void searchItems_thenFromIsZero_thenPageIsZero() {
        Long userId = 0L;
        int from = 0;
        int size = 1;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(getValidUser(userId)));
        when(itemRepository.searchByText(any(), any()))
                .thenReturn(Page.empty());

        itemService.searchItems(userId, "text", from, size);

        verify(itemRepository).searchByText(
                pageRequestArgumentCaptor.capture(),
                anyString()
        );
        assertEquals(0, pageRequestArgumentCaptor.getValue().getPageNumber());
    }

    @Test
    void searchItems_thenFromLessThanSize_thenPageIsZero() {
        Long userId = 0L;
        int from = 3;
        int size = 5;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(getValidUser(userId)));
        when(itemRepository.searchByText(any(), any()))
                .thenReturn(Page.empty());

        itemService.searchItems(userId, "text", from, size);

        verify(itemRepository).searchByText(
                pageRequestArgumentCaptor.capture(),
                anyString()
        );
        assertEquals(0, pageRequestArgumentCaptor.getValue().getPageNumber());
    }

    @Test
    void searchItems_thenFromMoreThanSize_thenPageIsFromDivideBySize() {
        Long userId = 0L;
        int from = 5;
        int size = 3;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(getValidUser(userId)));
        when(itemRepository.searchByText(any(), any()))
                .thenReturn(Page.empty());

        itemService.searchItems(userId, "text", from, size);

        verify(itemRepository).searchByText(
                pageRequestArgumentCaptor.capture(),
                anyString()
        );
        assertEquals(1, pageRequestArgumentCaptor.getValue().getPageNumber());
    }

    @Test
    void addComment_whenUserNotFound_thenNotExistsExceptionThrown() {
        Long userId = 0L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> itemService.addComment(userId, 0L, getValidCommentDto()),
                "User not found, but no NotExistsException thrown"
        );
    }

    @Test
    void addComment_whenItemNotFound_thenNotExistsExceptionThrown() {
        Long userId = 0L;
        Long itemId = 0L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(getValidUser(userId)));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(NotExistsException.class,
                () -> itemService.addComment(userId, itemId, getValidCommentDto()),
                "User not found, but no NotExistsException thrown"
        );
    }

    @Test
    void addComment_whenUserHasNoBookingsToItem_thenInvalidParamExceptionThrown() {
        Long userId = 0L;
        Long itemId = 0L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(getValidUser(userId)));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(getValidItem(itemId)));
        when(bookingRepository.findByStateAndBookerIdAndItemIdAndEndIsBefore(
                any(), anyLong(), anyLong(), any()))
                .thenReturn(List.of());

        assertThrows(InvalidParamException.class,
                () -> itemService.addComment(userId, itemId, getValidCommentDto()),
                "User not found, but no NotExistsException thrown"
        );
    }

    private User getValidUser(Long id) {
        return User.builder()
                .id(id)
                .name("userName")
                .email("email@email.ru")
                .build();
    }

    private Request getValidRequest(Long id) {
        return Request.builder()
                .requestId(id)
                .description("desc")
                .owner(getValidUser(0L))
                .created(LocalDateTime.now())
                .build();
    }

    private Item getValidItem(Long id) {
        return Item.builder()
                .id(id)
                .name("name")
                .description("desc")
                .available(true)
                .owner(getValidUser(1L))
                .request(getValidRequest(0L))
                .build();
    }

    private ItemDto getValidItemDto(Long id) {
        return ItemDto.builder()
                .id(id)
                .name("name")
                .description("desc")
                .available(true)
                .build();
    }

    private Booking getValidBooking() {
        return Booking.builder().build();
    }

    private LastNextBookingDto getValidLastNextBookingDto() {
        return LastNextBookingDto.builder().build();
    }

    private CommentDto getValidCommentDto() {
        return CommentDto.builder().build();
    }
}