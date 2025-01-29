package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {

    long id;

    private String name;

    private String description;

    private boolean available;

    private long owner;

    private long request;
}
