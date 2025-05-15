package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserDto userDto) {
        log.trace("Adding user is started");
        return userService.create(userDto);
    }

    @GetMapping("/{id}")
    public UserDto read(@PathVariable Long id) {
        log.trace("Getting user by id: {} is started", id);
        return userService.findById(id);
    }

    @GetMapping
    public Collection<UserDto> readAll() {
        log.trace("Getting collection of all users is started");
        return userService.findAll();
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id,
                          @RequestBody UserDto userDto) {
        log.trace("Updating user with id: {} is started", id);
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        log.trace("Deletion of user with id: {} is started", id);
        userService.deleteById(id);
    }
}
