package ru.practicum.shareit.exception;

public class BookingDeniedException extends RuntimeException {
    public BookingDeniedException(String message) {
        super(message);
    }
}
