package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class ItemServiceImplIntegrationTests {

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Transactional
    void add_whenInvokedAndItemHasRequest_thenItemWithRequestSavedToDB() {
        User itemOwner = saveRandomUser();
        Request request = savedRandomRequest();
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("name")
                .description("desc")
                .requestId(request.getRequestId())
                .available(true)
                .build();

        Long savedItemId = itemService.add(itemOwner.getId(), itemCreateDto).getId();

        Item savedItem = itemRepository.findById(savedItemId).get();
        assertThat(savedItem.getName(), equalTo(itemCreateDto.getName()));
        assertThat(savedItem.getDescription(), equalTo(itemCreateDto.getDescription()));
        assertThat(savedItem.getAvailable(), equalTo(itemCreateDto.getAvailable()));
        assertThat(savedItem.getRequest(), equalTo(request));
    }

    @Test
    void add_whenInvokedAndItemHasNoRequest_thenItemWithoutRequestSavedToDB() {
        User itemOwner = saveRandomUser();
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .build();

        Long savedItemId = itemService.add(itemOwner.getId(), itemCreateDto).getId();

        Item savedItem = itemRepository.findById(savedItemId).get();
        assertThat(savedItem.getName(), equalTo(itemCreateDto.getName()));
        assertThat(savedItem.getDescription(), equalTo(itemCreateDto.getDescription()));
        assertThat(savedItem.getAvailable(), equalTo(itemCreateDto.getAvailable()));
        assertThat(savedItem.getRequest(), nullValue());
    }

    @Test
    void add_whenInvokedAndItemHasNoRequest_thenDtoWithoutRequestIdReturned() {
        User itemOwner = saveRandomUser();
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .build();

        ItemDto addedItemDto = itemService.add(itemOwner.getId(), itemCreateDto);

        assertThat(addedItemDto.getRequestId(), nullValue());
    }

    @Test
    void update_whenInvokedAndUpdateHasRequest_thenUpdatedItemDtoWithRequestIdReturned() {
        User itemOwner = saveRandomUser();
        Request itemRequest = savedRandomRequest();
        Request newItemRequest = savedRandomRequest();
        Item savedItem = itemRepository.save(Item.builder()
                .name("oldName")
                .description("oldDescription")
                .available(true)
                .owner(itemOwner)
                .request(itemRequest)
                .build());
        ItemDto updateItemDto = ItemDto.builder()
                .name("newName")
                .description("newDescription")
                .available(false)
                .requestId(newItemRequest.getRequestId())
                .build();

        ItemDto updatedItemDto = itemService.update(itemOwner.getId(), savedItem.getId(), updateItemDto);

        assertThat(updatedItemDto.getName(), equalTo(updateItemDto.getName()));
        assertThat(updatedItemDto.getDescription(), equalTo(updateItemDto.getDescription()));
        assertThat(updatedItemDto.getAvailable(), equalTo(updateItemDto.getAvailable()));
        assertThat(updatedItemDto.getRequestId(), equalTo(newItemRequest.getRequestId()));
    }

    @Test
    void update_whenInvokedAndUpdateHasNoRequest_thenUpdatedItemDtoWithoutRequestIdReturned() {
        User itemOwner = saveRandomUser();
        Item savedItem = itemRepository.save(Item.builder()
                .name("oldName")
                .description("oldDescription")
                .available(true)
                .owner(itemOwner)
                .build());
        ItemDto updateItemDto = ItemDto.builder()
                .name("newName")
                .description("newDescription")
                .available(false)
                .build();

        ItemDto updatedItemDto = itemService.update(itemOwner.getId(), savedItem.getId(), updateItemDto);

        assertThat(updatedItemDto.getName(), equalTo(updateItemDto.getName()));
        assertThat(updatedItemDto.getDescription(), equalTo(updateItemDto.getDescription()));
        assertThat(updatedItemDto.getAvailable(), equalTo(updateItemDto.getAvailable()));
        assertThat(updatedItemDto.getRequestId(), nullValue());
    }

    @Test
    @Transactional
    void update_whenInvokedAndUpdateHasRequest_thenItemUpdatedInDBWithNewRequest() {
        User itemOwner = saveRandomUser();
        Request itemRequest = savedRandomRequest();
        Request newItemRequest = savedRandomRequest();
        Item savedItem = itemRepository.save(Item.builder()
                .name("oldName")
                .description("oldDescription")
                .available(true)
                .owner(itemOwner)
                .request(itemRequest)
                .build());
        ItemDto updateItemDto = ItemDto.builder()
                .name("newName")
                .description("newDescription")
                .available(false)
                .requestId(newItemRequest.getRequestId())
                .build();

        Long updatedItemId = itemService.update(itemOwner.getId(), savedItem.getId(), updateItemDto).getId();

        Item updatedItem = itemRepository.findById(updatedItemId).get();
        assertThat(updatedItem.getName(), equalTo(updateItemDto.getName()));
        assertThat(updatedItem.getDescription(), equalTo(updateItemDto.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(updateItemDto.getAvailable()));
        assertThat(updatedItem.getRequest(), equalTo(newItemRequest));
        assertThat(updatedItem.getOwner(), equalTo(itemOwner));
    }

    @Test
    @Transactional
    void update_whenSavedItemHasRequestAndUpdateHasNot_thenRequestRemovedFromItemInDb() {
        User itemOwner = saveRandomUser();
        Request itemRequest = savedRandomRequest();
        Item savedItem = itemRepository.save(Item.builder()
                .name("oldName")
                .description("oldDescription")
                .available(true)
                .owner(itemOwner)
                .request(itemRequest)
                .build());
        ItemDto updateItemDto = ItemDto.builder()
                .name("newName")
                .description("newDescription")
                .available(false)
                .build();

        Long updatedItemId = itemService.update(itemOwner.getId(), savedItem.getId(), updateItemDto).getId();

        Item updatedItem = itemRepository.findById(updatedItemId).get();
        assertThat(updatedItem.getName(), equalTo(updateItemDto.getName()));
        assertThat(updatedItem.getDescription(), equalTo(updateItemDto.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(updateItemDto.getAvailable()));
        assertThat(updatedItem.getRequest(), nullValue());
        assertThat(updatedItem.getOwner(), equalTo(itemOwner));
    }

    @Test
    @Transactional
    void update_whenSavedItemHasNoRequestAndAlsoUpdateHasNot_thenItemWithoutRequestSavedToDb() {
        User itemOwner = saveRandomUser();
        Item savedItem = itemRepository.save(Item.builder()
                .name("oldName")
                .description("oldDescription")
                .available(true)
                .owner(itemOwner)
                .build());
        ItemDto updateItemDto = ItemDto.builder()
                .name("newName")
                .description("newDescription")
                .available(false)
                .build();

        Long updatedItemId = itemService.update(itemOwner.getId(), savedItem.getId(), updateItemDto).getId();

        Item updatedItem = itemRepository.findById(updatedItemId).get();
        assertThat(updatedItem.getName(), equalTo(updateItemDto.getName()));
        assertThat(updatedItem.getDescription(), equalTo(updateItemDto.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(updateItemDto.getAvailable()));
        assertThat(updatedItem.getRequest(), nullValue());
        assertThat(updatedItem.getOwner(), equalTo(itemOwner));
    }

    @Test
    @Transactional
    void getById_whenItemHasComments_thenDtoWithCommentsReturned() {
        User owner = saveRandomUser();
        Item requestedItem = itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .owner(owner)
                .available(true)
                .build());
        Comment comment = commentRepository.save(Comment.builder()
                .item(requestedItem)
                .created(LocalDateTime.now())
                .author(saveRandomUser())
                .text("text")
                .build());

        ItemWithBookingsAndCommentsDto requestedItemDto = itemService.getById(owner.getId(), requestedItem.getId());

        assertThat(requestedItemDto.getComments(), hasSize(1));
        assertThat(requestedItemDto.getComments().get(0).getId(), equalTo(comment.getId()));

    }

    @Test
    void getById_whenRequestedUserIsOwner_thenDtoWithLastAndNexBookingsReturned() {
        User owner = saveRandomUser();
        Item requestedItem = itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .owner(owner)
                .available(true)
                .build());
        Booking lastBooking = Booking.builder()
                .item(requestedItem)
                .booker(saveRandomUser())
                .state(BookingState.APPROVED)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .build();
        Long lastBookingId = bookingRepository.save(lastBooking).getId();
        Booking nextBooking = Booking.builder()
                .item(requestedItem)
                .booker(saveRandomUser())
                .state(BookingState.APPROVED)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        Long nextBookingId = bookingRepository.save(nextBooking).getId();

        ItemWithBookingsAndCommentsDto requestedItemDto = itemService.getById(owner.getId(), requestedItem.getId());

        assertThat(requestedItemDto.getLastBooking(), notNullValue());
        assertThat(requestedItemDto.getLastBooking().getId(), equalTo(lastBookingId));
        assertThat(requestedItemDto.getNextBooking(), notNullValue());
        assertThat(requestedItemDto.getNextBooking().getId(), equalTo(nextBookingId));
    }

    @Test
    void getUserItems_whenInvoked_thenItemsDtoListWithBookingsReturned() {
        User owner = saveRandomUser();
        Item requestedItem_1 = itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .owner(owner)
                .available(true)
                .build());
        Item requestedItem_2 = itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .owner(owner)
                .available(true)
                .build());
        Booking lastBooking = Booking.builder()
                .item(requestedItem_1)
                .booker(saveRandomUser())
                .state(BookingState.APPROVED)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .build();
        Long lastBookingId = bookingRepository.save(lastBooking).getId();
        Booking nextBooking = Booking.builder()
                .item(requestedItem_2)
                .booker(saveRandomUser())
                .state(BookingState.APPROVED)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        Long nextBookingId = bookingRepository.save(nextBooking).getId();

        List<ItemWithBookingsAndCommentsDto> requestedItemDtos = itemService.getUserItems(owner.getId(), 0, 5);

        assertThat(requestedItemDtos, hasSize(2));
        assertThat(requestedItemDtos.get(0).getLastBooking().getId(), equalTo(lastBookingId));
        assertThat(requestedItemDtos.get(1).getNextBooking().getId(), equalTo(nextBookingId));
    }

    @Test
    @Transactional
    void getUserItems_whenItemHasComments_thenDtoListWithCommentsReturned() {
        User owner = saveRandomUser();
        Item requestedItem_1 = itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .owner(owner)
                .available(true)
                .build());
        itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .owner(owner)
                .available(true)
                .build());
        Comment comment = commentRepository.save(Comment.builder()
                .item(requestedItem_1)
                .created(LocalDateTime.now())
                .author(saveRandomUser())
                .text("text")
                .build());

        List<ItemWithBookingsAndCommentsDto> requestedItemDtos =
                itemService.getUserItems(owner.getId(), 0, 5);

        assertThat(requestedItemDtos, hasSize(2));
        assertThat(requestedItemDtos.get(0).getComments().get(0).getId(), equalTo(comment.getId()));
    }

    @Test
    void searchItems_whenInvoked_thenListItemsReturnedContainedTextInNameOrDescription() {
        User owner = saveRandomUser();
        Long requesterId = saveRandomUser().getId();
        String requestedText = "randomize";
        itemRepository.save(Item.builder()
                .name("SomeRandomizeName")
                .description("desc")
                .owner(owner)
                .available(true)
                .build());
        itemRepository.save(Item.builder()
                .name("name")
                .description("randoMize Description")
                .owner(owner)
                .available(true)
                .build());

        List<ItemDto> foundItems = itemService.searchItems(requesterId, requestedText, 0, 5);

        assertThat(foundItems, hasSize(2));
        assertThat(foundItems.get(0).getName(), containsStringIgnoringCase(requestedText));
        assertThat(foundItems.get(1).getDescription(), containsStringIgnoringCase(requestedText));
    }

    @Test
    @Transactional
    void addComment_whenInvoked_thenCommentAddedToItem() {
        User owner = saveRandomUser();
        User author = saveRandomUser();
        Item item = itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .owner(owner)
                .available(true)
                .build());
        bookingRepository.save(Booking.builder()
                .item(item)
                .booker(author)
                .state(BookingState.APPROVED)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .build());
        CommentDto commentDto = CommentDto.builder()
                .authorName("Author")
                .text("Some valid text")
                .build();

        CommentDto addedComment = itemService.addComment(author.getId(), item.getId(), commentDto);

        ItemWithBookingsAndCommentsDto itemDto =
                itemService.getById(owner.getId(), item.getId());
        assertThat(itemDto.getComments(), hasSize(1));
        assertThat(itemDto.getComments().get(0).getText(), equalTo(commentDto.getText()));
    }

    private User saveRandomUser() {
        return userRepository.save(User.builder()
                .name("name")
                .email(String.format("%s%s@email.ru", "email", new Random(9999L)))
                .build());
    }

    private Request savedRandomRequest() {
        return requestRepository.save(Request.builder()
                .owner(saveRandomUser())
                .description("desc")
                .created(LocalDateTime.now())
                .build());
    }
}