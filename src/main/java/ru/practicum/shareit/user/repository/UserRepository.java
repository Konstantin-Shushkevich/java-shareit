package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.Optional;

public interface UserRepository {

    UserDto save(User user);

    Optional<UserDto> findById(long id);

    UserDto update(User user);

    UserDto deleteById(long id);

    Optional<UserDto> findByEmail(String email);
}
