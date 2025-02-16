package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Not able to add comment with blank text")
    @Size(max = 255, message = "Maximum comment-text size exceeded (greater than 255)")
    String text;

    Long itemId;

    String authorName;

    LocalDateTime created;
}
