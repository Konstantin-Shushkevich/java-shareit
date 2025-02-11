package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item save(Item item) {
        log.trace("Start of saving item to repository (repository layer)");
        item.setId(getNextId());
        items.put(item.getId(), item);
        log.debug("Item with id {} was successfully saved", item.getId());
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        log.trace("Start of getting item by id: {} from repository layer", id);
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> findForTheUser(Long userId) {
        log.trace("Searching for items of user with id: {} has started at repository layer", userId);
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getAllItems() {
        log.trace("Getting of all items has started");
        return items.values();
    }

    @Override
    public Item update(Item item) {
        log.trace("Start of updating item at repository (repository layer)");
        long id = item.getId();
        items.put(id, item);
        log.debug("Item was successfully updated");
        return item;
    }

    @Override
    public Item deleteById(Long id) {
        log.trace("Start of deleting item with id: {} (repository layer)", id);
        return items.remove(id);
    }

    private long getNextId() {
        return items.keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
