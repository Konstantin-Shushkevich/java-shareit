package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemRequest {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long owner;

    private BookingDto nextBooking;

    private BookingDto lastBooking;

    private List<CommentDto> comments;
}
