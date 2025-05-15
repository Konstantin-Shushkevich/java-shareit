package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemRequestDto {

    private Long id;

    private String description;

    private Long requester;

    @JsonFormat
    private LocalDateTime created;

    private List<ItemDto> items;
}
