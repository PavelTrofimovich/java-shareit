package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.exceptions.UnknownStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String USER_ID = "X-Sharer-User-Id";

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader(value = USER_ID) Integer userId,
										 @RequestBody @Valid BookingDtoRequest bookingDto) {
		log.info("Создано новое бронирование");
		return bookingClient.addBooking(bookingDto, userId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approvedBooking(@RequestHeader(value = USER_ID) Integer userId,
											  @PathVariable Integer bookingId,
											  @RequestParam Boolean approved) {
		log.info("Запрос на подтверждение бронирования");
		return bookingClient.approvedBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(value = USER_ID) Integer userId,
										 @PathVariable Integer bookingId) {
		log.info("Запрос на получение бронирования {} для пользователя {}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(value = USER_ID) Integer userId,
											  @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
											  @RequestParam(name = "from", defaultValue = "0")
												  @PositiveOrZero Integer from,
											  @RequestParam(name = "size", defaultValue = "20")
												  @Positive Integer size) {
		BookingState state = BookingState.from(stateParam).orElseThrow(() -> new UnknownStateException(stateParam));
		log.info("Запрос на получение списка бронирований с состоянием {} для пользователя {}", state, userId);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsOwner(@RequestHeader(value = USER_ID) Integer userId,
												  @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
												  @RequestParam(name = "from", defaultValue = "0")
													  @PositiveOrZero Integer from,
												  @RequestParam(name = "size", defaultValue = "20")
													  @Positive Integer size) {
		BookingState state = BookingState.from(stateParam).orElseThrow(() -> new UnknownStateException(stateParam));
		log.info("Запрос на получение списка имеющихся у пользователя {} бронирований с состоянием {}", userId, state);
		return bookingClient.getBookingsOwner(userId, state, from, size);
	}
}