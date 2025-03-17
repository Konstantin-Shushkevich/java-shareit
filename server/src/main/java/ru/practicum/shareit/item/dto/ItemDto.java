package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long owner;

    private LocalDateTime start;
    private LocalDateTime end;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long requestId;
}
