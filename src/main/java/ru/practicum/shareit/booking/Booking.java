package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {

    long id;

    @NotNull
    @JsonFormat
    @FutureOrPresent
    LocalDateTime start;

    @NotNull
    @JsonFormat
    @Future
    LocalDateTime end;

    @NotNull
    Long item;

    @NotNull
    Long booker;

    @NotNull
    String status; //TODO сделать как enum
}
