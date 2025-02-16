package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.booking.BookingMapper.toBookingDto;
import static ru.practicum.shareit.booking.model.Status.REJECTED;
import static ru.practicum.shareit.item.CommentMapper.toComment;
import static ru.practicum.shareit.item.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.ItemMapper.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {

        log.trace("Adding item at service level has started");
        User user = validateUserIsInRepository(userId);
        log.debug("Item owner exists. Start of adding owner to item");
        Item item = toItemFromItemDto(itemDto);
        item.setOwner(user);
        log.trace("Owner is set. Item is ready to be added to repository");

        return toItemDtoFromItem(itemRepository.save(item));
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {

        log.trace("Adding comment by user with id: {} for item with id: {} is started (at service layer)",
                userId, itemId);

        User author = validateUserIsInRepository(userId);
        log.debug("User-commentator exists");

        validateTheAbilityToComment(userId);
        log.debug("User has rights to comment this item");

        Item item = toItemFromItemRequest(findById(itemId));

        return toCommentDto(commentRepository.save(toComment(commentDto, item, author)));
    }

    @Override
    public ItemRequest findById(Long id) {

        log.trace("Searching for item with id: {} has started (at service layer)", id);

        ItemRequest itemRequest = toItemRequestFromItem((itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Item with id = %d is not in repository", id)))));
        log.debug("Item with id: {} is in repository. Start of adding bookings and comments...", id);

        // TODO сделать выгрузку всего, чтобы 1 запросом?
        return addBookingsAndComments(itemRequest, LocalDateTime.now());
    }

    @Override
    public Collection<ItemRequest> findForTheUser(Long userId) {

        log.trace("Searching for items of user with id: {} has started (at service layer)", userId);
        validateUserIsInRepository(userId);
        log.debug("Item(s) owner with id: {} exists. Start of getting user's item(s)...", userId);

        LocalDateTime now = LocalDateTime.now();
        log.trace("Current Date-Time set: {}", now);

        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemRequestFromItem)
                .peek(itemRequest -> addBookingsAndComments(itemRequest, now))
                .toList();
    }

    @Override
    public ItemDto update(Long userId, Long id, ItemDto itemDto) {

        log.trace("Update of item with id: {} has started (at service layer)", id);

        validateUserIsInRepository(userId);
        log.debug("Item owner (id: {}) exists", userId);

        Item oldItemForUpdate = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        log.debug("Item with id: {} exists", id);

        if (!Objects.equals(oldItemForUpdate.getOwner().getId(), userId)) {
            log.error("User with id: {} is not owner of item with id {}", userId, id);
            throw new AccessDeniedException("User can't update someone else's item");
        }

        log.debug("The item can be updated. Start of update...");

        updateItemFields(oldItemForUpdate, itemDto);

        return toItemDtoFromItem(itemRepository.save(oldItemForUpdate));
    }

    @Override
    public void deleteById(Long id) {

        log.trace("Item, with id: {} deletion started (at service layer)", id);
        findById(id);
        log.debug("Item with id: {} is in repository and can be deleted", id);
        itemRepository.deleteById(id);
    }

    @Override
    public Collection<ItemDto> search(String text) {

        log.trace("Start of searching for items whose name or description contains text: {}", text);

        if (text.isBlank()) {
            log.debug("Text parameter is blank. Search is finished");
            return List.of();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDtoFromItem)
                .collect(Collectors.toList());
    }

    private User validateUserIsInRepository(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id: %d is not in repository", userId)));
    }

    private void validateTheAbilityToComment(Long userId) {
        bookingService.readBookingsForUser(userId, "PAST").stream()
                .filter(bookingResponse -> Objects.equals(bookingResponse.getBooker().getId(), userId) &&
                        bookingResponse.getEnd().isBefore(LocalDateTime.now()))
                .findFirst().orElseThrow(() -> new AccessDeniedException("User hasn't got rights to comment this item"));
    }

    private void updateItemFields(Item target, ItemDto source) {
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

    private ItemRequest addBookingsAndComments(ItemRequest itemRequest, LocalDateTime now) {

        itemRequest.setNextBooking(toBookingDto(bookingRepository
                .findTopBookingByItemIdAndStatusNotAndStartAfterOrderByStartAsc(itemRequest.getId(), REJECTED, now)));

        itemRequest.setLastBooking(toBookingDto(bookingRepository
                .findTopBookingByItemIdAndStatusNotAndStartBeforeOrderByEndDesc(itemRequest.getId(),
                        REJECTED, now)));

        itemRequest.setComments(commentRepository.findByItemId(itemRequest.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .toList());

        return itemRequest;
    }
}
