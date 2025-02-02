package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(Long id);

    Collection<Item> findForTheUser(Long userId);

    Collection<Item> getAllItems();

    Item update(Item item);

    Item delete(Long id);
}
