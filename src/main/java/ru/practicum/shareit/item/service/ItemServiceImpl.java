package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(long userId, Item item) {
        validateUser(userId);
        item.setOwner(userId);

        return itemRepository.save(item);
    }

    @Override
    public ItemDto findById(long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Item with id = %d is not in repository", id)));
    }

    @Override
    public Collection<ItemDto> findForTheUser(long userId) {
        validateUser(userId);
        return itemRepository.findForTheUser(userId);
    }

    @Override
    public ItemDto patch(long id, Item item) {
        validateUser(id);
        findById(item.getId());

        if (item.getOwner() != id) {
            throw new AccessDeniedException("User can't update someone else's item");
        }

        return itemRepository.patch(item);
    }

    @Override
    public ItemDto delete(long id) {
        findById(id);
        return itemRepository.delete(id);
    }

    @Override
    public Collection<ItemDto> search(String text) {

        if (text.isBlank()) {
            return List.of();
        }

        return itemRepository.getAllItems().stream()
                .filter(itemDto -> (containsIgnoreCase(text, itemDto.getName()) ||
                        containsIgnoreCase(text, itemDto.getDescription())) && itemDto.isAvailable())
                .collect(Collectors.toList());
    }

    private void validateUser(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id = %d is not in repository", userId)));
    }

    private boolean containsIgnoreCase(String text, String field) {
        return field.toLowerCase().contains(text.toLowerCase());
    }
}
