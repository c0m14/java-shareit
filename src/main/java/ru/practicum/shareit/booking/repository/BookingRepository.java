package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(
            Long bookerId,
            LocalDateTime end,
            LocalDateTime start,
            Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime date, Sort sort);

    List<Booking> findByStateAndBookerIdAndItemIdAndEndIsBefore(BookingState state,
                                                                Long bookerId,
                                                                Long ItemId,
                                                                LocalDateTime date);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime date, Sort sort);

    List<Booking> findByBookerIdAndState(Long bookerId, BookingState state);

    List<Booking> findByItemOwnerId(Long itemOwner, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
            Long bookerId,
            LocalDateTime end,
            LocalDateTime start,
            Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long itemOwner, LocalDateTime date, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long itemOwner, LocalDateTime date, Sort sort);

    List<Booking> findByItemOwnerIdAndState(Long itemOwner, BookingState state);

    Optional<Booking> findTopByStateAndItemIdAndStartIsBefore(BookingState state,
                                                              Long itemId,
                                                              LocalDateTime date,
                                                              Sort sort);

    Optional<Booking> findTopByStateAndItemIdAndStartAfter(BookingState state,
                                                           Long itemId,
                                                           LocalDateTime date,
                                                           Sort sort);
}
