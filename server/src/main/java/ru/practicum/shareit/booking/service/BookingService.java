package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.Collection;
import java.util.List;

public interface BookingService {
    BookingResponse create(Long userId, BookingDto bookingDto);

    BookingResponse findById(Long userId, Long bookingId);

    Collection<BookingResponse> readBookingsForOwner(Long userId, String state);

    List<BookingResponse> readBookingsForUser(Long userId, String state);

    BookingResponse updateStatus(Long userId, Long bookingId, Boolean approved);
}
