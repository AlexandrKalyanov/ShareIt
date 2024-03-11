package ru.practicum.shareit.item.dto;


import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

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

    public Item toItem(ItemRequestDto itemRequestDto, ItemRequest itemRequest) {
        return Item.builder()
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .request(itemRequest)
                .build();
    }

    public ItemForRequest toItemForRequest(Item item) {
        if (item.getRequest() == null) {
            return ItemForRequest.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .requestId(null)
                    .available(item.getAvailable())
                    .build();
        } else {
            return ItemForRequest.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .requestId(item.getRequest().getId())
                    .available(item.getAvailable())
                    .build();
        }
    }

    public ItemResponseDto itemToItemUpdateDto(Item item) {
        if (item.getRequest() == null) {
            return ItemResponseDto.builder()
                    .id(item.getId())
                    .description(item.getDescription())
                    .name(item.getName())
                    .available(item.getAvailable())
                    .requestId(null)
                    .build();
        } else {
            return ItemResponseDto.builder()
                    .id(item.getId())
                    .description(item.getDescription())
                    .name(item.getName())
                    .available(item.getAvailable())
                    .requestId(item.getRequest().getId())
                    .build();
        }
    }
}
