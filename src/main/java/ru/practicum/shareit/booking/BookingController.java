package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

import static ru.practicum.shareit.util.Constants.STATE_REGEX;
import static ru.practicum.shareit.util.Constants.USER_HEADER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@RequestHeader(USER_HEADER) Long userId,
                                  @RequestBody BookingDto bookingDto) {
        log.trace("Booking is started");
        return bookingService.create(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse read(@RequestHeader(USER_HEADER) Long userId,
                                @Positive @PathVariable Long bookingId) {
        log.trace("Getting booking with id: {} by user with id: {} is started", bookingId, userId);
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingResponse> readUserBookings(@RequestHeader(USER_HEADER) Long userId,
                                                        @Valid @Pattern(regexp = STATE_REGEX)
                                                        @RequestParam(required = false,
                                                                defaultValue = "ALL") String state) {
        log.trace("Getting collection of bookings for user-owner with id: {} is started. State is: {}", userId, state);
        return bookingService.readBookingsForUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingResponse> readUserAsOwnerBookings(@RequestHeader(USER_HEADER) Long userId,
                                                               @Valid @Pattern(regexp = STATE_REGEX)
                                                               @RequestParam(required = false,
                                                                       defaultValue = "ALL") String state) {
        log.trace("Getting collection of bookings for user-booker with id: {} is started. State is: {}",
                userId, state);
        return bookingService.readBookingsForOwner(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse updateStatus(@RequestHeader(USER_HEADER) Long userId,
                                        @PathVariable Long bookingId,
                                        @NotNull @RequestParam Boolean approved) {
        log.trace("Start of updating booking status (id: {}, userId: {})", bookingId, userId);
        return bookingService.updateStatus(userId, bookingId, approved);
    }
}
