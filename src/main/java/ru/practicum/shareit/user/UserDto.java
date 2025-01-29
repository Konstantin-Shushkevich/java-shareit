package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    long id;

    String name;

    private String email;
}
