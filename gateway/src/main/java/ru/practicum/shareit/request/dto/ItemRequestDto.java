package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ItemRequestDto {
    Long id;

    @NotNull
    @NotBlank
    @Size(max = 255)
    String description;

    @NotNull
    Long requester;

    @NotNull
    @JsonFormat
    @FutureOrPresent
    LocalDateTime created;
}
