package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequest {

    long id;

    @NotNull
    @NotBlank
    @Size(max = 255)
    String description;

    @NotNull
    Long requestor;

    @NotNull
    @JsonFormat
    @FutureOrPresent
    LocalDateTime created;
}
