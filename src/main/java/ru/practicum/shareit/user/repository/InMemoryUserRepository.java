package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public UserDto save(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Optional<UserDto> findById(long id) {
        User user = users.get(id);

        if (user == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(UserMapper.toUserDto(user));
    }

    @Override
    public UserDto update(User user) {
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto deleteById(long id) {
        return UserMapper.toUserDto(users.remove(id));
    }

    private long getNextId() {
        return users.keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L) + 1;
    }

    public Optional<UserDto> findByEmail(String email) {
        Optional<User> userByEmail = users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();

        if (userByEmail.isPresent()) {
            return Optional.of(UserMapper.toUserDto(userByEmail.get()));
        }

        return Optional.empty();
    }
}
