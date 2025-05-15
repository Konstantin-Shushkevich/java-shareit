package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Objects;

@Component
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {

        if (Objects.isNull(itemRequest)) {
            return null;
        }

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester().getId())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDto toItemRequestDtoWithItems(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .items(request.getItems().stream()
                        .map(ItemMapper::toItemDtoFromItem)
                        .toList())
                .build();
    }

    public static ItemRequest toItemRequestIfCreate(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();
    }
}
