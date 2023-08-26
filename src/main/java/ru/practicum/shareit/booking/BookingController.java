package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse addBooking(@RequestBody @Valid BookingDtoRequest bookingDto,
                                         @RequestHeader(value = USER_ID) Integer userId) {
        log.info("Создано новое бронирование {}", bookingDto);
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
        log.info("Было предоставлено бронирование {} для пользователя {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getBookings(@RequestHeader(value = USER_ID) Integer userId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        log.info("Для пользователя {} были найдены бронирование с состоянием {}", userId, state);
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getBookingsOwner(@RequestHeader(value = USER_ID) Integer userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        log.info("Для Владельца {} было найдено бронирование с состоянием {}", userId, state);
        return bookingService.getBookingsOwner(userId, state);
    }
}