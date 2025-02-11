package ru.practicum.shareit.booking;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
public class BookingDto {
    long id;

    LocalDateTime start;

    LocalDateTime end;

    Long item;

    Long booker;

    String status; //TODO сделать как enum
}
