package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemCreateDto toItemDto(Item item, BookingForItemDto last, BookingForItemDto next) {
        return new ItemCreateDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),last,next);
    }

    public static Item toItem(ItemCreateDto itemCreateDto) {
        return Item.builder()
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
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
