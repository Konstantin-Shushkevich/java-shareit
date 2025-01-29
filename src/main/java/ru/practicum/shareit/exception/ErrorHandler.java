package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponce handleNotFoundException(final NotFoundException e) {
        log.error("NotFoundException was thrown");
        return new ErrorResponce("Search was failed", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponce handleAccessDeniedException(final AccessDeniedException e) {
        log.error("AccessDeniedException was thrown");
        return new ErrorResponce("Access exception", e.getMessage());
    }
}
