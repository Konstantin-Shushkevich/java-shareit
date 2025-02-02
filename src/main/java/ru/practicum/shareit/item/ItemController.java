package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.validation.CreateItemValidation;
import ru.practicum.shareit.item.validation.PatchItemValidation;

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
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated(CreateItemValidation.class) @RequestBody ItemDto itemDto) {
        log.trace("Adding item is started");
        return itemService.create(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto read(@PathVariable Long itemId) {
        log.trace("Getting item by id: {} is started", itemId);
        return itemService.findById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> readForTheUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.trace("Getting items for user with id: {} is started", userId);
        return itemService.findForTheUser(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @Validated(PatchItemValidation.class) @RequestBody ItemDto itemDto) {
        log.trace("Updating item with id: {} is started", itemId);
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{id}")
    public ItemDto delete(@PathVariable Long id) {
        log.trace("Deletion of item with id: {} is started", id);
        return itemService.deleteById(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        log.trace("Search for items whose name or description contains text: {} is started", text);
        return itemService.search(text);
    }
}
