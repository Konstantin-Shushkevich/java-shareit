package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemDtoSpecified {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long owner;

    private BookingDto nextBooking;

    private BookingDto lastBooking;

    private List<CommentDto> comments;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long requestId;
}
