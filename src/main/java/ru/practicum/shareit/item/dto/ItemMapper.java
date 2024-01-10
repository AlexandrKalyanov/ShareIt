package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemCreateDto toItemDto(Item item) {
        return new ItemCreateDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public static Item toItem(long itemId, ItemCreateDto itemCreateDto, long userId) {
        return Item.builder()
                .id(itemId)
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .owner(userId)
                .build();
    }

    public static ItemUpdateDto itemToItemUpdateDto(Item item) {
        return ItemUpdateDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .name(item.getName())
                .available(item.getAvailable())
                .build();
    }
}
