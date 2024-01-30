package ru.practicum.shareit.item.dto;


import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@UtilityClass
public final class ItemMapper {
    public ItemResponseDto toItemResponseDto(Item item, BookingForItemDto last, BookingForItemDto next, Collection<CommentDTO> comments) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(last)
                .nextBooking(next)
                .comments(comments)
                .build();
    }

    public Item toItem(ItemRequestDto itemRequestDto) {
        return Item.builder()
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .build();
    }

    public ItemResponseDto itemToItemUpdateDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .name(item.getName())
                .available(item.getAvailable())
                .build();
    }
}
