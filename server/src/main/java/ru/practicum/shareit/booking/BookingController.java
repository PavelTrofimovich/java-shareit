package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse addBooking(@RequestHeader(value = USER_ID) Integer userId,
                                         @RequestBody BookingDtoRequest bookingDto) {
        log.info("Создано новое бронирование");
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approvedBooking(@RequestHeader(value = USER_ID) Integer userId,
                                              @PathVariable Integer bookingId,
                                              @RequestParam Boolean approved) {
        log.info("Запрос на подтверждение бронирования");
        return bookingService.approvedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@RequestHeader(value = USER_ID) Integer userId,
                                         @PathVariable Integer bookingId) {
        log.info("Запрос на получение бронирования {} для пользователя {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getBookings(@RequestHeader(value = USER_ID) Integer userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Запрос на получение списка бронирований с состоянием {} для пользователя {}", state, userId);
        return bookingService.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getBookingsOwner(@RequestHeader(value = USER_ID) Integer userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size) {
        log.info("Запрос на получение списка имеющихся у пользователя {} бронирований с состоянием {}", userId, state);
        return bookingService.getBookingsOwner(userId, state, from, size);
    }
}