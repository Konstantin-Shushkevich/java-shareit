package ru.practicum.shareit.exception;

public class UserNotValidToCommentException extends RuntimeException {
    public UserNotValidToCommentException(String message) {
        super(message);
    }
}
