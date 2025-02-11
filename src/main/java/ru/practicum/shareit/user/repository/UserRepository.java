package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    User update(User user);

    User deleteById(Long id);
}
