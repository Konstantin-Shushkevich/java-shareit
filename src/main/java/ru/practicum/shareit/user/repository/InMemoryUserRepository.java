package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("User with id {} was successfully saved", user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.of(users.get(id));
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User deleteById(long id) {
        return users.remove(id);
    }

    private long getNextId() {
        return users.keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L) + 1;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByName(String name) {
        return users.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst();
    }
}
