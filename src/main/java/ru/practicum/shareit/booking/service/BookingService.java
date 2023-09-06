package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse addBooking(BookingDtoRequest dto, Integer userId);

    BookingDtoResponse approvedBooking(Integer userId, Integer bookingId, Boolean approved);

    BookingDtoResponse getBooking(Integer userId, Integer bookingId);

    List<BookingDtoResponse> getBookings(Integer userId, String state, int from, int size);

    List<BookingDtoResponse> getBookingsOwner(Integer userId, String state, int from, int size);
}