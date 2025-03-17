package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserEmailNotUniqueException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserEmailNotUniqueException("User with email: " + userDto.getEmail() + " is already exists");
        }

        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Override
    public UserDto findById(Long id) {
        log.trace("Searching for user with id: {} has started (at service layer)", id);
        return toUserDto(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("User with id = %d is not in repository", id))));
    }

    @Override
    public Collection<UserDto> findAll() {
        log.trace("Getting collection of users has started (at service layer)");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.trace("Update of user with id: {} has started (at service layer)", id);
        UserDto oldUser = findById(id);
        log.debug("User with id {} is in the database and can be updated", id);

        if (userDto.getName() == null) {
            userDto.setName(oldUser.getName());
            log.debug("User name was updated");
        }

        if (userDto.getEmail() == null) {
            userDto.setEmail(oldUser.getEmail());
            log.debug("User email was updated");
        }

        userDto.setId(id);
        log.debug("User fields update is finished. Set id: {}", id);

        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Override
    public void deleteById(Long id) {
        log.trace("User with id: {} deletion started (at service layer)", id);
        findById(id);
        log.debug("User with id: {} is in repository and can be delete", id);

        userRepository.deleteById(id);
    }

}
