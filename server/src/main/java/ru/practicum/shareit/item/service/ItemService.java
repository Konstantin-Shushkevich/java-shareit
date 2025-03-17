package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoSpecified;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);

    ItemDtoSpecified findById(Long userId, Long id);

    Collection<ItemDtoSpecified> findForTheUser(Long userId);

    ItemDto update(Long userId, Long id, ItemDto itemDto);

    void deleteById(Long id);

    Collection<ItemDto> search(String text);
}
