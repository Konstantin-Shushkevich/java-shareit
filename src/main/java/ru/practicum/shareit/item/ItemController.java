package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.validation.CreateItemValidation;
import ru.practicum.shareit.item.validation.PatchItemValidation;

import static ru.practicum.shareit.util.Constants.USER_HEADER;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(USER_HEADER) Long userId,
                          @Validated(CreateItemValidation.class) @RequestBody ItemDto itemDto) {
        log.trace("Adding item is started");
        return itemService.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_HEADER) Long userId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.trace("Adding comment by user with id: {} for item with id: {} is started", userId, itemId);
        return itemService.createComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ItemRequest read(@PathVariable Long itemId) { //todo с отзывами
        log.trace("Getting item by id: {} is started", itemId);
        return itemService.findById(itemId);
    }

    @GetMapping
    public Collection<ItemRequest> readForTheUser(@RequestHeader(USER_HEADER) Long userId) {
        log.trace("Getting items for user with id: {} is started", userId);
        return itemService.findForTheUser(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @Validated(PatchItemValidation.class) @RequestBody ItemDto itemDto) {
        log.trace("Updating item with id: {} is started", itemId);
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.trace("Deletion of item with id: {} is started", id);
        itemService.deleteById(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        log.trace("Search for items whose name or description contains text: {} is started", text);
        return itemService.search(text);
    }
}
