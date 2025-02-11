package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto findById(Long id);

    Collection<ItemDto> findForTheUser(Long userId);

    ItemDto update(Long userId, Long id, ItemDto itemDto);

    ItemDto deleteById(Long id);

    Collection<ItemDto> search(String text);
}
