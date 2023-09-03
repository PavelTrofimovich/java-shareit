package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findAllByBookerIdAndStatus(Integer userId, Status status, Pageable page);

    Page<Booking> findAllByBookerId(Integer userId, Pageable page);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Integer userId, LocalDateTime dateTime1,
                                                                             LocalDateTime dateTime2, Pageable page);

    Page<Booking> findAllByBookerIdAndStartAfter(Integer userId, LocalDateTime dateTime, Pageable page);

    Page<Booking> findAllByBookerIdAndEndBefore(Integer userId, LocalDateTime dateTime, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndStatus(Integer userId, Status status, Pageable page);

    Page<Booking> findAllByItemOwnerId(Integer userId, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Integer userId, LocalDateTime dateTime1,
                                                                                LocalDateTime dateTime2, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndStartAfter(Integer userId, LocalDateTime dateTime,
                                                                    Pageable page);

    Page<Booking> findAllByItemOwnerIdAndEndBefore(Integer userId, LocalDateTime dateTime,
                                                                   Pageable page);

    BookingDto findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(Integer itemId,
                                                                         LocalDateTime now,
                                                                         Status status);

    BookingDto findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Integer itemId,
                                                                        LocalDateTime dateTime,
                                                                        Status status);

    List<Booking> findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(Integer itemId, Integer bookerId,
                                                                    Status status, LocalDateTime dateTime);

    List<BookingDto> findAllByItemInAndStartAfterAndStatusNot(List<Item> items, LocalDateTime now, Status status,
                                                              Sort sort);

    List<BookingDto> findAllByItemInAndStartBeforeAndStatus(List<Item> items, LocalDateTime now, Status status,
                                                            Sort sort);
}