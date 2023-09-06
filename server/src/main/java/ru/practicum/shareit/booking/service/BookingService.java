package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDtoResponse addBooking(BookingDtoRequest dto, Integer userId);

    BookingDtoResponse approvedBooking(Integer userId, Integer bookingId, Boolean approved);

    BookingDtoResponse getBooking(Integer userId, Integer bookingId);

    List<BookingDtoResponse> getBookings(Integer userId, State state, int from, int size);

    List<BookingDtoResponse> getBookingsOwner(Integer userId, State state, int from, int size);
}