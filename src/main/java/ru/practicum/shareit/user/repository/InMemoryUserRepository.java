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
        log.trace("Start of saving user to repository (repository layer)");
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("User with id {} was successfully saved", user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        log.trace("Start of getting user by id: {} from repository layer", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.trace("Start of getting user by email from repository (repository layer)");
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByName(String name) {
        log.trace("Start of getting user by name from repository (repository layer)");
        return users.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst();
    }

    @Override
    public User update(User user) {
        log.trace("Start of updating user at repository (repository layer)");
        users.put(user.getId(), user);
        log.debug("User was successfully updated");
        return user;
    }

    @Override
    public User deleteById(Long id) {
        log.trace("Start of deleting user with id: {} (repository layer)", id);
        return users.remove(id);
    }

    private long getNextId() {
        return users.keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
