package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @Transactional
    void add_whenInvoked_thenBookingSavedToBD() {
        User booker = saveRandomUser();
        Long bookerId = booker.getId();
        User itemOwner = saveRandomUser();
        Item item = saveRandomItem(itemOwner);
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        Long savedBookingId = bookingService.add(bookerId, bookingCreationDto).getId();

        Booking savedBooking = bookingRepository.findById(savedBookingId).get();
        assertThat(savedBooking.getBooker(), equalTo(booker));
        assertThat(savedBooking.getItem(), equalTo(item));
        assertThat(savedBooking.getStart(), equalTo(bookingCreationDto.getStart()));
        assertThat(savedBooking.getEnd(), equalTo(bookingCreationDto.getEnd()));
    }

    @Test
    void add_whenInvoked_thenBookingDtoReturned() {
        User booker = saveRandomUser();
        Long bookerId = booker.getId();
        User itemOwner = saveRandomUser();
        Item item = saveRandomItem(itemOwner);
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto returnedBooking = bookingService.add(bookerId, bookingCreationDto);

        assertThat(returnedBooking.getBooker().getId(), equalTo(bookerId));
        assertThat(returnedBooking.getItem().getId(), equalTo(item.getId()));
        assertThat(returnedBooking.getStart(), equalTo(bookingCreationDto.getStart()));
        assertThat(returnedBooking.getEnd(), equalTo(bookingCreationDto.getEnd()));
    }

    @Test
    @Transactional
    void changeStatus_whenInvoked_thenBookingWithChangedStatusSavedToDB() {
        User booker = saveRandomUser();
        Long bookerId = booker.getId();
        User itemOwner = saveRandomUser();
        Item item = saveRandomItem(itemOwner);
        boolean approved = true;
        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .state(BookingState.WAITING)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build());

        bookingService.changeStatus(itemOwner.getId(), booking.getId(), approved);

        Booking updatedBooking = bookingRepository.findById(booking.getId()).get();
        assertThat(updatedBooking.getId(), equalTo(booking.getId()));
        assertThat(updatedBooking.getBooker(), equalTo(booker));
        assertThat(updatedBooking.getItem(), equalTo(item));
        assertThat(updatedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(updatedBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(updatedBooking.getState(), equalTo(BookingState.APPROVED));
    }

    @Test
    void changeStatus_whenInvoked_thenBookingDtoWithChangedStatusReturned() {
        User booker = saveRandomUser();
        Long bookerId = booker.getId();
        User itemOwner = saveRandomUser();
        Item item = saveRandomItem(itemOwner);
        boolean approved = true;
        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .state(BookingState.WAITING)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build());

        BookingDto returnedBooking = bookingService.changeStatus(itemOwner.getId(), booking.getId(), approved);

        assertThat(returnedBooking.getId(), equalTo(booking.getId()));
        assertThat(returnedBooking.getBooker().getId(), equalTo(bookerId));
        assertThat(returnedBooking.getItem().getId(), equalTo(item.getId()));
        assertThat(returnedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(returnedBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(returnedBooking.getStatus(), equalTo(BookingState.APPROVED));
    }

    @Test
    @Transactional
    void getById_whenInvoked_thenBookingDtoReturned() {
        User booker = saveRandomUser();
        Long bookerId = booker.getId();
        User itemOwner = saveRandomUser();
        Item item = saveRandomItem(itemOwner);
        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .state(BookingState.WAITING)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build());

        BookingDto returnedBooking = bookingService.getById(bookerId, booking.getId());

        assertThat(returnedBooking.getId(), equalTo(booking.getId()));
        assertThat(returnedBooking.getBooker().getId(), equalTo(bookerId));
        assertThat(returnedBooking.getItem().getId(), equalTo(item.getId()));
        assertThat(returnedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(returnedBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(returnedBooking.getStatus(), equalTo(BookingState.WAITING));
    }

    @Test
    @Transactional
    void getByStateBooker_whenStatusIsAll_thenAllBookersBookingsReturned() {
        //clearingContext
        bookingRepository.deleteAll();
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");
        String searchedStatus = "all";
        int countOfPossibleStatuses = 5;

        List<BookingDto> requestedBookings = bookingService.getByStateBooker(bookerId, searchedStatus, 0, 20);

        assertThat(requestedBookings, hasSize(countOfPossibleStatuses));
    }

    @Test
    @Transactional
    void getByStateBooker_whenStatusIsCurrent_thenCurrentBookersBookingsReturned() {
        //clearingContext
        bookingRepository.deleteAll();
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");
        String searchedStatus = "current";

        List<BookingDto> requestedBookings = bookingService.getByStateBooker(bookerId, searchedStatus, 0, 20);

        BookingDto foundBooking = requestedBookings.get(0);
        assertTrue(foundBooking.getStart().isBefore(LocalDateTime.now()));
        assertTrue(foundBooking.getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @Transactional
    void getByStateBooker_whenStatusIsPast_thenPastBookersBookingsReturned() {
        //clearingContext
        bookingRepository.deleteAll();
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");
        String searchedStatus = "past";

        List<BookingDto> requestedBookings = bookingService.getByStateBooker(bookerId, searchedStatus, 0, 20);

        BookingDto foundBooking = requestedBookings.get(0);
        assertTrue(foundBooking.getStart().isBefore(LocalDateTime.now()));
        assertTrue(foundBooking.getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @Transactional
    void getByStateBooker_whenStatusIsFuture_thenFutureBookersBookingsReturned() {
        //clearingContext
        bookingRepository.deleteAll();
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");
        String searchedStatus = "future";

        List<BookingDto> requestedBookings = bookingService.getByStateBooker(bookerId, searchedStatus, 0, 20);

        BookingDto foundBooking = requestedBookings.get(0);
        assertTrue(foundBooking.getStart().isAfter(LocalDateTime.now()));
        assertTrue(foundBooking.getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @Transactional
    void getByStateBooker_whenStatusIsWaiting_thenWaitingBookersBookingsReturned() {
        //clearingContext
        bookingRepository.deleteAll();
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");
        String searchedStatus = "waiting";

        List<BookingDto> requestedBookings = bookingService.getByStateBooker(bookerId, searchedStatus, 0, 20);

        BookingDto foundBooking = requestedBookings.get(0);
        assertThat(foundBooking.getStatus(), equalTo(BookingState.WAITING));
    }

    @Test
    @Transactional
    void getByStateBooker_whenStatusIsRejected_thenRejectedBookersBookingsReturned() {
        //clearingContext
        bookingRepository.deleteAll();
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");
        String searchedStatus = "rejected";

        List<BookingDto> requestedBookings = bookingService.getByStateBooker(bookerId, searchedStatus, 0, 20);

        BookingDto foundBooking = requestedBookings.get(0);
        assertThat(foundBooking.getStatus(), equalTo(BookingState.REJECTED));
    }

    @Test
    @Transactional
    void getByStateOwner_whenStatusIsAll_thenAllBookersBookingsReturned() {
        //clearingContext
        bookingRepository.deleteAll();
        Long itemOwnerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");
        String searchedStatus = "all";
        int countOfPossibleStatuses = 5;

        List<BookingDto> requestedBookings = bookingService.getByStateOwner(itemOwnerId, searchedStatus, 0, 20);

        assertThat(requestedBookings, hasSize(countOfPossibleStatuses));
    }

    @Test
    @Transactional
    void getByStateOwner_whenStatusIsCurrent_thenCurrentBookersBookingsReturned() {
        //clearingContext
        bookingRepository.deleteAll();
        Long itemOwnerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");
        String searchedStatus = "current";

        List<BookingDto> requestedBookings = bookingService.getByStateOwner(itemOwnerId, searchedStatus, 0, 20);

        BookingDto foundBooking = requestedBookings.get(0);
        assertTrue(foundBooking.getStart().isBefore(LocalDateTime.now()));
        assertTrue(foundBooking.getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @Transactional
    void getByStateOwner_whenStatusIsPast_thenPastBookersBookingsReturned() {
        //clearingContext
        bookingRepository.deleteAll();
        Long itemOwnerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");
        String searchedStatus = "past";

        List<BookingDto> requestedBookings = bookingService.getByStateOwner(itemOwnerId, searchedStatus, 0, 20);

        BookingDto foundBooking = requestedBookings.get(0);
        assertTrue(foundBooking.getStart().isBefore(LocalDateTime.now()));
        assertTrue(foundBooking.getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @Transactional
    void getByStateOwner_whenStatusIsFuture_thenFutureBookersBookingsReturned() {
        //clearingContext
        bookingRepository.deleteAll();
        Long itemOwnerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");
        String searchedStatus = "future";

        List<BookingDto> requestedBookings = bookingService.getByStateOwner(itemOwnerId, searchedStatus, 0, 20);

        BookingDto foundBooking = requestedBookings.get(0);
        assertTrue(foundBooking.getStart().isAfter(LocalDateTime.now()));
        assertTrue(foundBooking.getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @Transactional
    void getByStateOwner_whenStatusIsWaiting_thenWaitingBookersBookingsReturned() {
        //clearingContext
        bookingRepository.deleteAll();
        Long itemOwnerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");
        String searchedStatus = "waiting";

        List<BookingDto> requestedBookings = bookingService.getByStateOwner(itemOwnerId, searchedStatus, 0, 20);

        BookingDto foundBooking = requestedBookings.get(0);
        assertThat(foundBooking.getStatus(), equalTo(BookingState.WAITING));
    }

    @Test
    @Transactional
    void getByStateOwner_whenStatusIsRejected_thenRejectedBookersBookingsReturned() {
        //clearingContext
        bookingRepository.deleteAll();
        Long itemOwnerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");
        String searchedStatus = "rejected";

        List<BookingDto> requestedBookings = bookingService.getByStateOwner(itemOwnerId, searchedStatus, 0, 20);

        BookingDto foundBooking = requestedBookings.get(0);
        assertThat(foundBooking.getStatus(), equalTo(BookingState.REJECTED));
    }

    private User saveRandomUser() {
        return userRepository.save(User.builder()
                .name("name")
                .email(String.format("%s%s@email.ru", "email", new Random(9999L)))
                .build());
    }

    private Item saveRandomItem(User owner) {
        return itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());
    }

    private Map<String, Long> saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner() {
        User itemOwner = saveRandomUser();
        User booker = saveRandomUser();

        //current
        bookingRepository.save(Booking.builder()
                .item(saveRandomItem(itemOwner))
                .booker(booker)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .state(BookingState.APPROVED)
                .build());

        //past
        bookingRepository.save(Booking.builder()
                .item(saveRandomItem(itemOwner))
                .booker(booker)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .state(BookingState.APPROVED)
                .build());

        //future
        bookingRepository.save(Booking.builder()
                .item(saveRandomItem(itemOwner))
                .booker(booker)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .state(BookingState.APPROVED)
                .build());

        //waiting
        bookingRepository.save(Booking.builder()
                .item(saveRandomItem(itemOwner))
                .booker(booker)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .state(BookingState.WAITING)
                .build());

        //rejected
        bookingRepository.save(Booking.builder()
                .item(saveRandomItem(itemOwner))
                .booker(booker)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .state(BookingState.REJECTED)
                .build());

        return Map.of("ItemOwnerId", itemOwner.getId(),
                "BookerId", booker.getId());
    }
}