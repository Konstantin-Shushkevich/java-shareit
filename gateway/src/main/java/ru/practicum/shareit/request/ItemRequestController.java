package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static ru.practicum.shareit.util.Constants.USER_HEADER;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) Long userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        log.trace("Adding item-request is started");
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> readAllByUser(@RequestHeader(USER_HEADER) Long userId) {
        log.trace("Getting item-requests of user with id: {} is started", userId);
        return itemRequestClient.readAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> readAllByOtherUsers(@RequestHeader(USER_HEADER) Long userId) {
        log.trace("Getting item-requests for all users except user with id: {} is started", userId);
        return itemRequestClient.readAllByOtherUsers(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> readTheItemRequest(@PathVariable Long requestId) {
        log.trace("Getting item-request with id: {} is started", requestId);
        return itemRequestClient.readTheItemRequest(requestId);
    }
}
