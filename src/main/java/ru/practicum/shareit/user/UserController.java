package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.validation.CreateUserValidation;
import ru.practicum.shareit.user.validation.PatchUserValidation;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Validated(CreateUserValidation.class) @RequestBody UserDto userDto) {
        log.trace("Adding user is started");
        return userService.create(userDto);
    }

    @GetMapping("/{id}")
    public UserDto read(@Positive @PathVariable Long id) {
        log.trace("Getting user by id: {} is started", id);
        return userService.findById(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Positive @PathVariable Long id,
                          @Validated(PatchUserValidation.class) @RequestBody UserDto userDto) {
        log.trace("Updating user with id: {} is started", id);
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public UserDto deleteById(@Positive @PathVariable Long id) {
        log.trace("Deletion of user with id: {} is started", id);
        return userService.deleteById(id);
    }
}
