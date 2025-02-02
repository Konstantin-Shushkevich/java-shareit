package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto findById(long id);

    UserDto update(long id, UserDto userDto);

    UserDto delete(long id);
}
