package ru.practicum.shareit.user.dto;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserDto {

    private Long id;

    private String name;

    private String email;
}
