package ru.practicum.shareit.user;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class User {

    private Long id;

    private String name;

    private String email;
}
