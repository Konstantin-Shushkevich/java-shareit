package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

public interface UserService {

    UserDto create(User user);

    UserDto findById(long id);

    UserDto update(User user);

    UserDto delete(long id);
}
