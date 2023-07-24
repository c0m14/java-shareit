package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerId(Pageable page, Long bookerId);

    Page<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(
            Pageable page,
            Long bookerId,
            LocalDateTime end,
            LocalDateTime start);

    Page<Booking> findByBookerIdAndEndIsBefore(Pageable page, Long bookerId, LocalDateTime date);

    List<Booking> findByStateAndBookerIdAndItemIdAndEndIsBefore(BookingState state,
                                                                Long bookerId,
                                                                Long itemId,
                                                                LocalDateTime date);

    Page<Booking> findByBookerIdAndStartIsAfter(Pageable page, Long bookerId, LocalDateTime date);

    Page<Booking> findByBookerIdAndState(Pageable page, Long bookerId, BookingState state);

    Page<Booking> findByItemOwnerId(Pageable page, Long itemOwner);

    Page<Booking> findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
            Pageable page,
            Long bookerId,
            LocalDateTime end,
            LocalDateTime start);

    Page<Booking> findByItemOwnerIdAndEndIsBefore(Pageable page, Long itemOwner, LocalDateTime date);

    Page<Booking> findByItemOwnerIdAndStartIsAfter(Pageable page, Long itemOwner, LocalDateTime date);

    Page<Booking> findByItemOwnerIdAndState(Pageable page, Long itemOwner, BookingState state);

    Optional<Booking> findTopByStateAndItemIdAndStartIsBefore(BookingState state,
                                                              Long itemId,
                                                              LocalDateTime date,
                                                              Sort sort);

    Optional<Booking> findTopByStateAndItemIdAndStartAfter(BookingState state,
                                                           Long itemId,
                                                           LocalDateTime date,
                                                           Sort sort);
}
