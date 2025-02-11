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
        log.trace("Adding item at service level has started");
        validateUser(userId);
        log.debug("Item owner exists. Start of adding owner to item");
        itemDto.setOwner(userId);

        return toItemDto(itemRepository.save(toItem(itemDto)));
    }

    @Override
    public ItemDto findById(Long id) {
        log.trace("Searching for item with id: {} has started (at service layer)", id);
        return toItemDto(itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Item with id = %d is not in repository", id))));
    }

    @Override
    public Collection<ItemDto> findForTheUser(Long userId) {
        log.trace("Searching for items of user with id: {} has started (at service layer)", userId);
        validateUser(userId);
        log.debug("Item(s) owner (id: {}) exists. Start of getting user's item(s)", userId);
        return itemRepository.findForTheUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        log.trace("Update of item with id: {} has started (at service layer)", id);
        validateUser(userId);
        log.debug("Item owner (id: {}) exists", userId);
        ItemDto oldItemForUpdate = findById(id);
        log.debug("Item with id: {} exists", id);

        if (!Objects.equals(oldItemForUpdate.getOwner(), userId)) {
            log.error("User with id: {} is not owner of item with id {}", userId, id);
            throw new AccessDeniedException("User can't update someone else's item");
        }

        log.debug("The item can be updated. Start of update...");

        updateItemFields(oldItemForUpdate, itemDto);

        return toItemDto(itemRepository.update(toItem(oldItemForUpdate)));
    }

    @Override
    public ItemDto deleteById(Long id) {
        log.trace("Item, with id: {} deletion started (at service layer)", id);
        findById(id);
        log.debug("Item with id: {} is in repository and can be deleted", id);
        return toItemDto(itemRepository.deleteById(id));
    }

    @Override
    public Collection<ItemDto> search(String text) {
        log.trace("Start of searching for items whose name or description contains text: {}", text);

        if (text.isBlank()) {
            log.debug("Text parameter is blank. Search is finished");
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
        log.debug("Start of fields update");

        if (source.getName() != null) {
            target.setName(source.getName());
            log.debug("Item name was updated");
        }

        if (source.getDescription() != null) {
            target.setDescription(source.getDescription());
            log.debug("Item description was updated");
        }

        if (source.getAvailable() != null) {
            target.setAvailable(source.getAvailable());
            log.debug("Item available status was updated");
        }

        log.debug("Item fields update is finished");
    }

    private boolean containsIgnoreCase(String text, String field) {
        return field.toLowerCase().contains(text.toLowerCase());
    }
}
