package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final String userHeader = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@RequestHeader(userHeader) Long userId,
                                  @RequestBody BookingDto bookingDto) {
        log.trace("Booking is started");
        return bookingService.create(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse read(@RequestHeader(userHeader) Long userId,
                                @PathVariable Long bookingId) {
        log.trace("Getting booking with id: {} by user with id: {} is started", bookingId, userId);
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingResponse> readUserBookings(@RequestHeader(userHeader) Long userId,
                                                        @RequestParam String state) {
        log.trace("Getting collection of bookings for user-owner with id: {} is started. State is: {}", userId, state);
        return bookingService.readBookingsForUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingResponse> readUserAsOwnerBookings(@RequestHeader(userHeader) Long userId,
                                                               @RequestParam String state) {
        log.trace("Getting collection of bookings for user-booker with id: {} is started. State is: {}",
                userId, state);
        return bookingService.readBookingsForOwner(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse updateStatus(@RequestHeader(userHeader) Long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam Boolean approved) {
        log.trace("Start of updating booking status (id: {}, userId: {})", bookingId, userId);
        return bookingService.updateStatus(userId, bookingId, approved);
    }
}
