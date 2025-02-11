package ru.practicum.shareit.item;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Item {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long owner;

    private Long request;
}
