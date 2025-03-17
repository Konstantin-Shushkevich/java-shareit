package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.validation.CreateUserValidation;
import ru.practicum.shareit.user.validation.PatchUserValidation;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Validated(CreateUserValidation.class) @RequestBody UserDto userDto) {
        log.trace("Adding user is started");
        return userClient.create(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> read(@Positive @PathVariable Long id) {
        log.trace("Getting user by id: {} is started", id);
        return userClient.findById(id);
    }

    @GetMapping
    public ResponseEntity<Object> readAll() {
        log.trace("Getting collection of all users is started");
        return userClient.findAll();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Positive @PathVariable Long id,
                                         @Validated(PatchUserValidation.class) @RequestBody UserDto userDto) {
        log.trace("Updating user with id: {} is started", id);
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@Positive @PathVariable Long id) {
        log.trace("Deletion of user with id: {} is started", id);
        return userClient.deleteById(id);
    }
}
