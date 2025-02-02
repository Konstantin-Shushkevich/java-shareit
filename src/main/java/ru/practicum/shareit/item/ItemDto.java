package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.validation.CreateItemValidation;
import ru.practicum.shareit.item.validation.PatchItemValidation;

@Data
@Builder
public class ItemDto {

    private long id;

    @NotNull(groups = CreateItemValidation.class, message = "Not able to add item if name is NULL")
    @NotBlank(groups = CreateItemValidation.class, message = "Not able to add item with blank name")
    @Size(groups = {CreateItemValidation.class, PatchItemValidation.class}, max = 50)
    private String name;

    @NotNull(groups = CreateItemValidation.class, message = "Not able to add item if description is NULL")
    @NotBlank(groups = CreateItemValidation.class, message = "Not able to add item with blank name")
    @Size(groups = {CreateItemValidation.class, PatchItemValidation.class}, max = 255)
    private String description;

    @NotNull(groups = CreateItemValidation.class, message = "Not able to add item if available-status is NULL")
    private Boolean available;

    private long owner;

    private long request;
}
