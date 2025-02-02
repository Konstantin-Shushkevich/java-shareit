package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(long id);

    Collection<Item> findForTheUser(long userId);

    Collection<Item> getAllItems();

    Item update(Item item);

    Item delete(long id);
}
