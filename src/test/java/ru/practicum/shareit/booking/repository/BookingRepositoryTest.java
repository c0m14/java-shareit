package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void beforeEach() {
        bookingRepository.deleteAll();

    }

    @Test
    void testFindByBookerId() {
        User booker = saveRandomUser();
        Item item = saveRandomItem(saveRandomUser());
        PageRequest pageRequest = PageRequest.of(1, 1);
        bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .state(BookingState.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build());
        Booking booking2 = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .state(BookingState.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build());

        Page<Booking> bookings = bookingRepository.findByBookerId(pageRequest, booker.getId());

        assertThat(bookings.getTotalPages(), equalTo(2));
        assertThat(bookings.getTotalElements(), equalTo(2L));
        assertEquals(booking2, bookings.getContent().get(0));
    }

    @Test
    void testFindByBookerIdAndEndIsAfterAndStartIsBefore() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now();

        Page<Booking> bookings = bookingRepository
                .findByBookerIdAndEndIsAfterAndStartIsBefore(pageRequest, bookerId, end, start);

        Booking current = bookings.getContent().get(0);
        assertThat(bookings.getTotalPages(), equalTo(1));
        assertThat(bookings.getTotalElements(), equalTo(1L));
        assertThat(current.getBooker().getId(), equalTo(bookerId));
        assertTrue(current.getStart().isBefore(LocalDateTime.now()));
        assertTrue(current.getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    void testFindByBookerIdAndEndIsBefore() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");
        LocalDateTime end = LocalDateTime.now();

        Page<Booking> bookings = bookingRepository
                .findByBookerIdAndEndIsBefore(pageRequest, bookerId, end);

        Booking past = bookings.getContent().get(0);
        assertThat(bookings.getTotalPages(), equalTo(1));
        assertThat(bookings.getTotalElements(), equalTo(1L));
        assertThat(past.getBooker().getId(), equalTo(bookerId));
        assertTrue(past.getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void testFindByStateAndBookerIdAndItemIdAndEndIsBefore() {
        User booker = saveRandomUser();
        Item item = saveRandomItem(saveRandomUser());
        bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .state(BookingState.APPROVED)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2))
                .build());
        LocalDateTime date = LocalDateTime.now();

        List<Booking> bookings = bookingRepository
                .findByStateAndBookerIdAndItemIdAndEndIsBefore(BookingState.APPROVED, booker.getId(), item.getId(), date);

        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getState(), equalTo(BookingState.APPROVED));
        assertThat(foundBooking.getBooker(), equalTo(booker));
        assertThat(foundBooking.getItem(), equalTo(item));
        assertTrue(foundBooking.getEnd().isBefore(date));
    }

    @Test
    void testFindByBookerIdAndStartIsAfter() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");
        LocalDateTime start = LocalDateTime.now();

        Page<Booking> bookings = bookingRepository
                .findByBookerIdAndStartIsAfter(pageRequest, bookerId, start);

        Booking future = bookings.getContent().get(0);
        assertThat(future.getBooker().getId(), equalTo(bookerId));
        assertTrue(future.getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void testFindByBookerIdAndState() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");

        Page<Booking> bookings = bookingRepository
                .findByBookerIdAndState(pageRequest, bookerId, BookingState.WAITING);

        Booking booking = bookings.getContent().get(0);
        assertThat(bookings.getTotalPages(), equalTo(1));
        assertThat(bookings.getTotalElements(), equalTo(1L));
        assertThat(booking.getBooker().getId(), equalTo(bookerId));
        assertThat(booking.getState(), equalTo(BookingState.WAITING));
    }

    @Test
    void testFindByItemOwnerId() {
        User itemOwner = saveRandomUser();
        Item item = saveRandomItem(itemOwner);
        PageRequest pageRequest = PageRequest.of(1, 1);
        bookingRepository.save(Booking.builder()
                .booker(saveRandomUser())
                .item(item)
                .state(BookingState.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build());
        Booking booking2 = bookingRepository.save(Booking.builder()
                .booker(saveRandomUser())
                .item(item)
                .state(BookingState.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build());

        Page<Booking> bookings = bookingRepository.findByItemOwnerId(pageRequest, itemOwner.getId());

        assertThat(bookings.getTotalPages(), equalTo(2));
        assertThat(bookings.getTotalElements(), equalTo(2L));
        assertEquals(booking2, bookings.getContent().get(0));
    }

    @Test
    void testFindByItemOwnerIdAndEndIsAfterAndStartIsBefore() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long ownerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now();

        Page<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndEndIsAfterAndStartIsBefore(pageRequest, ownerId, end, start);

        Booking current = bookings.getContent().get(0);
        assertThat(bookings.getTotalPages(), equalTo(1));
        assertThat(bookings.getTotalElements(), equalTo(1L));
        assertThat(current.getItem().getOwner().getId(), equalTo(ownerId));
        assertTrue(current.getStart().isBefore(LocalDateTime.now()));
        assertTrue(current.getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    void testFindByItemOwnerIdAndEndIsBefore() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long ownerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");
        LocalDateTime end = LocalDateTime.now();

        Page<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndEndIsBefore(pageRequest, ownerId, end);

        Booking past = bookings.getContent().get(0);
        assertThat(bookings.getTotalPages(), equalTo(1));
        assertThat(bookings.getTotalElements(), equalTo(1L));
        assertThat(past.getItem().getOwner().getId(), equalTo(ownerId));
        assertTrue(past.getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void testFindByItemOwnerIdAndStartIsAfter() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long ownerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");
        LocalDateTime start = LocalDateTime.now();

        Page<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndStartIsAfter(pageRequest, ownerId, start);

        Booking future = bookings.getContent().get(0);
        assertThat(future.getItem().getOwner().getId(), equalTo(ownerId));
        assertTrue(future.getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void testFindByItemOwnerIdAndState() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long ownerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");

        Page<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndState(pageRequest, ownerId, BookingState.WAITING);

        Booking booking = bookings.getContent().get(0);
        assertThat(bookings.getTotalPages(), equalTo(1));
        assertThat(bookings.getTotalElements(), equalTo(1L));
        assertThat(booking.getItem().getOwner().getId(), equalTo(ownerId));
        assertThat(booking.getState(), equalTo(BookingState.WAITING));
    }

    @Test
    void testFindTopByStateAndItemIdAndStartIsBefore() {
        Item item = saveRandomItem(saveRandomUser());
        Booking notLastBooking = bookingRepository.save(Booking.builder()
                .item(item)
                .state(BookingState.APPROVED)
                .booker(saveRandomUser())
                .start(LocalDateTime.now().minusHours(3))
                .end(LocalDateTime.now().minusHours(2))
                .build());
        Booking lastBooking = bookingRepository.save(Booking.builder()
                .item(item)
                .state(BookingState.APPROVED)
                .booker(saveRandomUser())
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .build());

        Optional<Booking> last = bookingRepository
                .findTopByStateAndItemIdAndStartIsBefore(BookingState.APPROVED,
                        item.getId(),
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "end"));

        assertTrue(last.isPresent());
        assertThat(last.get(), equalTo(lastBooking));
    }

    @Test
    void testFindTopByStateAndItemIdAndStartAfter() {
        Item item = saveRandomItem(saveRandomUser());
        Booking notNextBooking = bookingRepository.save(Booking.builder()
                .item(item)
                .state(BookingState.APPROVED)
                .booker(saveRandomUser())
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(3))
                .build());
        Booking nextBooking = bookingRepository.save(Booking.builder()
                .item(item)
                .state(BookingState.APPROVED)
                .booker(saveRandomUser())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build());

        Optional<Booking> next = bookingRepository
                .findTopByStateAndItemIdAndStartAfter(BookingState.APPROVED,
                        item.getId(),
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.ASC, "start"));

        assertTrue(next.isPresent());
        assertThat(next.get(), equalTo(nextBooking));
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