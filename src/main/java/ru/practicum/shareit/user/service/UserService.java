package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto findById(Long id);

    UserDto update(Long id, UserDto userDto);

    UserDto delete(Long id);
}
