package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> readAllByUser(Long userId);

    List<ItemRequestDto> readAllByOtherUsers(Long userId);

    ItemRequestDto readTheItemRequest(Long requestId);
}
