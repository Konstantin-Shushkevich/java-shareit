package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CommentDto {

    Long id;

    String text;

    Long itemId;

    String authorName;

    LocalDateTime created;
}
