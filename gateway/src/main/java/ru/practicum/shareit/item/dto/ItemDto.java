package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.item.validation.CreateItemValidation;
import ru.practicum.shareit.item.validation.PatchItemValidation;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemDto {

    private Long id;

    @NotBlank(groups = CreateItemValidation.class, message = "Not able to add item with blank name")
    @Size(groups = {CreateItemValidation.class, PatchItemValidation.class}, max = 50)
    private String name;

    @NotBlank(groups = CreateItemValidation.class, message = "Not able to add item with blank name")
    @Size(groups = {CreateItemValidation.class, PatchItemValidation.class}, max = 255)
    private String description;

    @NotNull(groups = CreateItemValidation.class, message = "Not able to add item if available-status is NULL")
    private Boolean available;

    private Long owner;

    private LocalDateTime start;
    private LocalDateTime end;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long requestId;
}
