package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Attempt to add a user whose email address was previously registered");
        }

        return userRepository.save(user);
    }

    @Override
    public UserDto findById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("User with id = %d is not in repository", id)));
    }

    @Override
    public UserDto update(User user) {
        findById(user.getId());
        return userRepository.update(user);
    }

    @Override
    public UserDto delete(long id) {
        findById(id);
        return userRepository.deleteById(id);
    }
}
