package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto findById(Long id);

    Collection<UserDto> findAll();

    UserDto update(Long id, UserDto userDto);

    void deleteById(Long id);
}
