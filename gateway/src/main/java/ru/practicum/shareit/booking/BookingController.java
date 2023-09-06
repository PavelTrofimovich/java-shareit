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
	public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID) Integer userId,
												@RequestBody @Valid BookingDtoRequest bookingDto) {
		log.info("Creating Booking {}, userId={}", bookingDto, userId);
		return bookingClient.addBooking(bookingDto, userId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approvedBooking(@PathVariable @Positive Integer bookingId,
													 @RequestParam(name = "approved") Boolean approved,
													 @RequestHeader(USER_ID) Integer ownerId) {
		log.info("Approving {} Booking with ID={} and ownerId={}", approved, bookingId, ownerId);
		return bookingClient.approvedBooking(ownerId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) Integer userId,
											 @PathVariable Integer bookingId) {
		log.info("Get Booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID) Integer userId,
															@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
															@RequestParam(name = "from", defaultValue = "0")
															@PositiveOrZero Integer from,
															@RequestParam(name = "size", defaultValue = "20")
															@Positive Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnknownStateException(stateParam));
		log.info("Get Booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsOwner(@RequestHeader(USER_ID) Integer ownerId,
														   @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
														   @RequestParam(name = "from", defaultValue = "0")
														   @PositiveOrZero Integer from,
														   @RequestParam(name = "size", defaultValue = "20")
														   @Positive Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get Booking with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
		return bookingClient.getBookingsOwner(ownerId, state, from, size);
	}
}