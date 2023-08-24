package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.id = :bookingId " +
            "AND (b.item.owner.id = :userId OR b.booker.id = :userId)")
    Optional<Booking> findBookingByOwnerOrBooker(Integer bookingId, Integer userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer userId, Status status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Integer userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer userId, LocalDateTime dateTime1,
                                                                             LocalDateTime dateTime2);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Integer userId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer userId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Integer userId, Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Integer userId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer userId, LocalDateTime dateTime1,
                                                                                LocalDateTime dateTime2);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Integer userId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer userId, LocalDateTime dateTime);

    BookingDto findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(Integer itemId,
                                                                         LocalDateTime now,
                                                                         Status status);

    BookingDto findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Integer itemId,
                                                                        LocalDateTime dateTime,
                                                                        Status status);

    List<Booking> findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(Integer itemId, Integer bookerId,
                                                                    Status status, LocalDateTime dateTime);
}