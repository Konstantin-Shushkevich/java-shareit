package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.validation.CreateItemValidation;
import ru.practicum.shareit.item.validation.PatchItemValidation;

import static ru.practicum.shareit.util.Constants.user_header;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Positive @RequestHeader(user_header) Long userId,
                                         @Validated(CreateItemValidation.class) @RequestBody ItemDto itemDto) {
        log.trace("Adding item is started");
        return itemClient.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Positive @RequestHeader(user_header) Long userId,
                                                @Positive @PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.trace("Adding comment by user with id: {} for item with id: {} is started", userId, itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> read(@Positive @RequestHeader(user_header) Long userId,
                                       @Positive @PathVariable Long itemId) {
        log.trace("Getting item by id: {} is started", itemId);
        return itemClient.findById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> readForTheUser(@Positive @RequestHeader(user_header) Long userId) {
        log.trace("Getting items for user with id: {} is started", userId);
        return itemClient.findForTheUser(userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Positive @RequestHeader(user_header) Long userId,
                                         @Positive @PathVariable Long itemId,
                                         @Validated(PatchItemValidation.class) @RequestBody ItemDto itemDto) {
        log.trace("Updating item with id: {} is started", itemId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@Positive @PathVariable Long id) {
        log.trace("Deletion of item with id: {} is started", id);
        return itemClient.deleteById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.trace("Search for items whose name or description contains text: {} is started", text);
        return itemClient.search(text);
    }
}
