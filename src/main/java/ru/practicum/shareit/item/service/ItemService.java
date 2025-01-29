package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(long userId, Item item);

    ItemDto findById(long id);

    Collection<ItemDto> findForTheUser(long userId);

    ItemDto patch(long id, Item item);

    ItemDto delete(long id);

    Collection<ItemDto> search(String text);
}
