package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static ru.practicum.shareit.user.UserMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        log.trace("Adding user at service level has started");
        checkIfUserNameOrEmailAreInRepositoryIfCreate(userDto);

        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Override
    public UserDto findById(Long id) {
        log.trace("Searching for user with id: {} has started (at service layer)", id);
        return toUserDto(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("User with id = %d is not in repository", id))));
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

        log.debug("User fields update is finished");

        checkIfUserNameOrEmailAreInRepositoryIfUpdate(userDto);

        return toUserDto(userRepository.update(toUser(userDto)));
    }

    @Override
    public UserDto deleteById(Long id) {
        log.trace("User with id: {} deletion started (at service layer)", id);
        findById(id);
        log.debug("User with id: {} is in repository and can be delete", id);

        return toUserDto(userRepository.deleteById(id));
    }

    private void checkIfUserNameOrEmailAreInRepositoryIfCreate(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent() ||
                userRepository.findByName(userDto.getName()).isPresent()) {
            log.error("The user's name or email already exists in the repository");
            throw new IllegalArgumentException("Attempt to add a user whose email or name was previously registered");
        }
        log.debug("User's name and email are valid (are not in repository yet)");
    }

    private void checkIfUserNameOrEmailAreInRepositoryIfUpdate(UserDto userDto) {
        long id = userDto.getId();

        Optional<User> userByEmailOpt = userRepository.findByEmail(userDto.getEmail());

        if (userByEmailOpt.isPresent() && userByEmailOpt.get().getId() != id) {
            log.error("Email {} is already taken by another user", userDto.getEmail());
            throw new IllegalArgumentException("Email is already in use");
        }

        Optional<User> userByNameOpt = userRepository.findByName(userDto.getName());

        if (userByNameOpt.isPresent() && userByNameOpt.get().getId() != id) {
            log.error("Name {} is already taken by another user", userDto.getName());
            throw new IllegalArgumentException("Name is already in use");
        }

        log.debug("User's name and email are valid");
    }
}
