package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import static ru.practicum.shareit.util.Constants.STATE_REGEX;
import static ru.practicum.shareit.util.Constants.user_header;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> create(@Positive @RequestHeader(user_header) Long userId,
										 @Valid @RequestBody BookingDto bookingDto) {
		log.trace("Booking is started");
		return bookingClient.create(userId, bookingDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> read(@Positive @RequestHeader(user_header) Long userId,
									   @Positive @PathVariable Long bookingId) {
		log.trace("Getting booking with id: {} by user with id: {} is started", bookingId, userId);
		return bookingClient.findById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> readUserBookings(@Positive @RequestHeader(user_header) Long userId,
												   @Valid @Pattern(regexp = STATE_REGEX)
												   @RequestParam(required = false,
														   defaultValue = "ALL") String state) {
		log.trace("Getting collection of bookings for user-owner with id: {} is started. State is: {}", userId, state);
		return bookingClient.getBookings(userId, state);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> readUserAsOwnerBookings(@Positive @RequestHeader(user_header) Long userId,
														  @Valid @Pattern(regexp = STATE_REGEX)
														  @RequestParam(required = false,
																  defaultValue = "ALL") String state) {
		log.trace("Getting collection of bookings for user-booker with id: {} is started. State is: {}",
				userId, state);
		return bookingClient.getBookings(userId, state);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateStatus(@Positive @RequestHeader(user_header) Long userId,
											   @Positive @PathVariable Long bookingId,
											   @NotNull @RequestParam Boolean approved) {
		log.trace("Start of updating booking status (id: {}, userId: {})", bookingId, userId);
		return bookingClient.updateStatus(userId, bookingId, approved);
	}
}
