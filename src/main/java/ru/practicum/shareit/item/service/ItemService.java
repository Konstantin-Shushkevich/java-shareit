package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto findById(long id);

    Collection<ItemDto> findForTheUser(long userId);

    ItemDto update(long userId, long id, ItemDto itemDto);

    ItemDto delete(long id);

    Collection<ItemDto> search(String text);
}
