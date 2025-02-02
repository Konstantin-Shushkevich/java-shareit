package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import static ru.practicum.shareit.item.ItemMapper.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        validateUser(userId);
        itemDto.setOwner(userId);

        return toItemDto(itemRepository.save(toItem(itemDto)));
    }

    @Override
    public ItemDto findById(Long id) {
        return toItemDto(itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Item with id = %d is not in repository", id))));
    }

    @Override
    public Collection<ItemDto> findForTheUser(Long userId) {
        validateUser(userId);
        return itemRepository.findForTheUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        validateUser(userId);
        ItemDto oldItemForUpdate = findById(id);

        if (!Objects.equals(oldItemForUpdate.getOwner(), userId)) {
            throw new AccessDeniedException("User can't update someone else's item");
        }

        updateItemFields(oldItemForUpdate, itemDto);

        return toItemDto(itemRepository.update(toItem(oldItemForUpdate)));
    }

    @Override
    public ItemDto delete(Long id) {
        findById(id);
        return toItemDto(itemRepository.delete(id));
    }

    @Override
    public Collection<ItemDto> search(String text) {

        if (text.isBlank()) {
            return List.of();
        }

        return itemRepository.getAllItems().stream()
                .filter(item -> (containsIgnoreCase(text, item.getName()) ||
                        containsIgnoreCase(text, item.getDescription())) && item.getAvailable())
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id = %d is not in repository", userId)));
    }

    private void updateItemFields(ItemDto target, ItemDto source) {
        if (source.getName() != null) target.setName(source.getName());
        if (source.getDescription() != null) target.setDescription(source.getDescription());
        if (source.getAvailable() != null) target.setAvailable(source.getAvailable());
    }

    private boolean containsIgnoreCase(String text, String field) {
        return field.toLowerCase().contains(text.toLowerCase());
    }
}
