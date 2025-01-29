package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody Item item) {
        return itemService.create(userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto read(@PathVariable long id) {
        return itemService.findById(id);
    }

    @GetMapping
    public Collection<ItemDto> readForTheUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findForTheUser(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long id, @RequestBody Item item) { // TODO валидировать только избранные поля?
        return itemService.patch(id, item);
    }

    @DeleteMapping("/{id}")
    public ItemDto delete(@PathVariable long id) {
        return itemService.delete(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
