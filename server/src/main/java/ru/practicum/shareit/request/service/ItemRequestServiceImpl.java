package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequestIfCreate;
import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequestDto;
import static ru.practicum.shareit.user.UserMapper.toUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User requester = toUser(userService.findById(userId));
        ItemRequest itemRequest = toItemRequestIfCreate(itemRequestDto);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        return toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> readAllByUser(Long userId) {
        userService.findById(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequester_IdOrderByCreatedDesc(userId);
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .filter(Objects::nonNull)
                .peek(itemRequestDto ->
                        itemRequestDto.setItems(
                                itemRepository.findItemByRequestId(itemRequestDto.getId()).stream()
                                        .map(ItemMapper::toItemDtoFromItem).toList()
                        )
                )
                .toList();
    }

    @Override
    public List<ItemRequestDto> readAllByOtherUsers(Long userId) {
        userService.findById(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequester_IdNotOrderByCreatedDesc(userId);

        if (!itemRequests.isEmpty()) {
            return itemRequests.stream()
                    .map(ItemRequestMapper::toItemRequestDto)
                    .filter(Objects::nonNull)
                    .peek(itemRequestDto ->
                            itemRequestDto.setItems(
                                    itemRepository.findItemByRequestId(itemRequestDto.getId()).stream()
                                            .map(ItemMapper::toItemDtoFromItem).toList()
                            )
                    )
                    .toList();
        }

        return List.of();
    }

    @Override
    public ItemRequestDto readTheItemRequest(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .map(itemRequest -> {
                    ItemRequestDto dto = toItemRequestDto(itemRequest);
                    List<ItemDto> items = itemRepository.findItemByRequestId(itemRequest.getId())
                            .stream()
                            .map(ItemMapper::toItemDtoFromItem)
                            .toList();

                    return Optional.ofNullable(dto)
                            .map(d -> {
                                d.setItems(items);
                                return d;
                            })
                            .orElse(null);
                })
                .orElseThrow(() -> new NotFoundException("There's no item-request"));
    }
}
