package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import static org.springframework.data.domain.Sort.Direction.DESC;

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
            throw new UserNotHaveThisItem("Пользователь не является владельцем вещи");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.save(BookingMapper.toBooking(dto, user, item));
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Transactional
    @Override
    public BookingDtoResponse approvedBooking(Integer userId, Integer bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new UserNotHaveThisItem("Пользователь не является владельцем вещи");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Объект Booking имеет отличный статус от WAITING");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoResponse getBooking(Integer userId, Integer bookingId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Брони с такими парамметрами не найдена");
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getBookings(Integer userId, State state, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        PageRequest page = PageRequest.of(from / size, size, Sort.by(DESC, "start"));

        LocalDateTime timeNow = LocalDateTime.now();
        List<Booking> listBookings = new ArrayList<>();
        switch (state) {
            case WAITING:
                listBookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, page).toList();
                break;
            case APPROVED:
                listBookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.APPROVED, page).toList();
                break;
            case REJECTED:
                listBookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, page).toList();
                break;
            case ALL:
                listBookings = bookingRepository.findAllByBookerId(userId, page).toList();
                break;
            case CURRENT:
                listBookings = bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfter(userId, timeNow, timeNow, page).toList();
                break;
            case FUTURE:
                listBookings = bookingRepository
                        .findAllByBookerIdAndStartAfter(userId, timeNow, page).toList();
                break;
            case PAST:
                listBookings = bookingRepository
                        .findAllByBookerIdAndEndBefore(userId, timeNow, page).toList();
                break;
        }
        List<BookingDtoResponse> list = new ArrayList<>();
        listBookings.forEach(booking -> list.add(BookingMapper.toBookingDtoResponse(booking)));
        return list;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getBookingsOwner(Integer userId, State state, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        PageRequest page = PageRequest.of(from / size, size, Sort.by(DESC, "start"));
        LocalDateTime timeNow = LocalDateTime.now();
        List<Booking> listBookings = new ArrayList<>();
        switch (state) {
            case WAITING:
                listBookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.WAITING, page).toList();
                break;
            case APPROVED:
                listBookings = bookingRepository
                        .findAllByItemOwnerIdAndStatus(userId, Status.APPROVED, page).toList();
                break;
            case REJECTED:
                listBookings = bookingRepository
                        .findAllByItemOwnerIdAndStatus(userId, Status.REJECTED, page).toList();
                break;
            case ALL:
                listBookings = bookingRepository.findAllByItemOwnerId(userId, page).toList();
                break;
            case CURRENT:
                listBookings = bookingRepository
                        .findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId, timeNow, timeNow, page).toList();
                break;
            case FUTURE:
                listBookings = bookingRepository
                        .findAllByItemOwnerIdAndStartAfter(userId, timeNow, page).toList();
                break;
            case PAST:
                listBookings = bookingRepository
                        .findAllByItemOwnerIdAndEndBefore(userId, timeNow, page).toList();
                break;
        }
        List<BookingDtoResponse> list = new ArrayList<>();
        listBookings.forEach(booking -> list.add(BookingMapper.toBookingDtoResponse(booking)));
        return list;
    }
}