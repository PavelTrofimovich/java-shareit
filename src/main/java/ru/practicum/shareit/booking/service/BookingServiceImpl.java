package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.UnknownStateException;
import ru.practicum.shareit.exception.exceptions.UserNotHaveThisItem;
import ru.practicum.shareit.exception.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public BookingDtoResponse addBooking(BookingDtoRequest dto, Integer userId) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (!item.getAvailable()) {
            throw new ValidationException("Объект не доступен");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new UserNotHaveThisItem();
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.save(BookingMapper.toBooking(dto, user, item));
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDtoResponse approvedBooking(Integer userId, Integer bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new UserNotHaveThisItem();
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Объект Booking имеет отличный статус от WAITING");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDtoResponse getBooking(Integer userId, Integer bookingId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        Booking booking = bookingRepository.findBookingByOwnerOrBooker(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public List<BookingDtoResponse> getBookings(Integer userId, String stateStr) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        LocalDateTime timeNow = LocalDateTime.now();
        List<Booking> listBookings = new ArrayList<>();
        State state = checkState(stateStr);
        switch (state) {
            case WAITING:
                listBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case APPROVED:
                listBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.APPROVED);
                break;
            case REJECTED:
                listBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            case ALL:
                listBookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                listBookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        timeNow, timeNow);
                break;
            case FUTURE:
                listBookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, timeNow);
                break;
            case PAST:
                listBookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, timeNow);
                break;
        }
        List<BookingDtoResponse> list = new ArrayList<>();
        listBookings.forEach(booking -> list.add(BookingMapper.toBookingDto(booking)));
        return list;
    }

    @Transactional
    @Override
    public List<BookingDtoResponse> getBookingsOwner(Integer userId, String stateStr) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        LocalDateTime timeNow = LocalDateTime.now();
        State state = checkState(stateStr);
        List<Booking> listBookings = new ArrayList<>();
        switch (state) {
            case WAITING:
                listBookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case APPROVED:
                listBookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.APPROVED);
                break;
            case REJECTED:
                listBookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            case ALL:
                listBookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                listBookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        timeNow, timeNow);
                break;
            case FUTURE:
                listBookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, timeNow);
                break;
            case PAST:
                listBookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, timeNow);
                break;
        }
        List<BookingDtoResponse> list = new ArrayList<>();
        listBookings.forEach(booking -> list.add(BookingMapper.toBookingDto(booking)));
        return list;
    }

    private State checkState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException(state);
        }
    }
}