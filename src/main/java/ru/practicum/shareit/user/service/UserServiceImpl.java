package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import static ru.practicum.shareit.user.UserMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {

        checkIfUserNameOrEmailAreInRepository(userDto);

        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Override
    public UserDto findById(long id) {
        return toUserDto(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("User with id = %d is not in repository", id))));
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        UserDto oldUser = findById(id);

        if (userDto.getName() == null) userDto.setName(oldUser.getName());
        if (userDto.getEmail() == null) userDto.setName(oldUser.getEmail());
        userDto.setId(id);

        return toUserDto(userRepository.update(toUser(userDto)));
    }

    @Override
    public UserDto delete(long id) {
        findById(id);
        return toUserDto(userRepository.deleteById(id));
    }

    private void checkIfUserNameOrEmailAreInRepository(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent() ||
                userRepository.findByName(userDto.getName()).isPresent()) {
            throw new IllegalArgumentException("Attempt to add a user whose email or name was previously registered");
        }
    }
}
