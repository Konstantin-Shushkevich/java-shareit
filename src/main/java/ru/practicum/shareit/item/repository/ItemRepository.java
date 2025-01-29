package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    ItemDto save(Item item);

    Optional<ItemDto> findById(long id);

    Collection<ItemDto> findForTheUser(long userId);

    Collection<ItemDto> getAllItems();

    ItemDto patch(Item item);

    ItemDto delete(long id);
}
