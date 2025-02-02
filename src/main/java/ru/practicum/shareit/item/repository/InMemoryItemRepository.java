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
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> findForTheUser(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getAllItems() {
        return items.values();
    }

    @Override
    public Item update(Item item) {
        long id = item.getId();
        items.put(id, item);
        return findById(id).get();
    }

    @Override
    public Item delete(Long id) {
        return items.remove(id);
    }

    private long getNextId() {
        return items.keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
