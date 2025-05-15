package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final String userHeader = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader(userHeader) Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.trace("Adding item-request is started");

        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> readAllByUser(@RequestHeader(userHeader) Long userId) {
        log.trace("Getting item-requests of user with id: {} is started", userId);
        return itemRequestService.readAllByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> readAllByOtherUsers(@RequestHeader(userHeader) Long userId) {
        log.trace("Getting item-requests for all users except user with id: {} is started", userId);
        return itemRequestService.readAllByOtherUsers(userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto readTheItemRequest(@PathVariable Long requestId) {
        log.trace("Getting item-request with id: {} is started", requestId);
        return itemRequestService.readTheItemRequest(requestId);
    }
}
