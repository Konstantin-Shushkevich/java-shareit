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
        return itemService.create(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto read(@PathVariable Long id) {
        return itemService.findById(id);
    }

    @GetMapping
    public Collection<ItemDto> readForTheUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findForTheUser(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long id,
                          @Validated(PatchItemValidation.class) @RequestBody ItemDto itemDto) {
        return itemService.update(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public ItemDto delete(@PathVariable Long id) {
        return itemService.delete(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
