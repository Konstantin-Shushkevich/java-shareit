package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public ItemDto save(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Optional<ItemDto> findById(long id) {
        return Optional.ofNullable(ItemMapper.toItemDto(items.get(id)));
    }

    @Override
    public Collection<ItemDto> findForTheUser(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getAllItems() {
        return items.values().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto patch(Item item) {
        long id = item.getId();
        items.put(id, item);
        return findById(id).get();
    }

    @Override
    public ItemDto delete(long id) {
        return ItemMapper.toItemDto(items.remove(id));
    }

    private long getNextId() {
        return items.keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
