package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class BookingDto {

    private Long id;

    @JsonFormat
    private LocalDateTime start;

    @JsonFormat
    private LocalDateTime end;

    private Long itemId;

    private Long bookerId;

    private Status status;
}
