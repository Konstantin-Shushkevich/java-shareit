package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoSpecified;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public static ItemDto toItemDtoFromItem(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .build();
    }

    public static Item toItemFromItemDto(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Item toItemFromItemRequest(ItemDtoSpecified itemDtoSpecified) {
        return Item.builder()
                .id(itemDtoSpecified.getId())
                .name(itemDtoSpecified.getName())
                .description(itemDtoSpecified.getDescription())
                .available(itemDtoSpecified.getAvailable())
                .build();
    }

    public static ItemDtoSpecified toItemDtoSpecifiedFromItem(Item item) {
        return ItemDtoSpecified.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .build();
    }
}
