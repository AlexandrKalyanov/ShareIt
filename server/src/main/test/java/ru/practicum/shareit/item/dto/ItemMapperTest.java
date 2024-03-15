package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemResponseDto() {
        User user = new User(1L, "Name", "mail@mail.ru");
        Item item = new Item(1L, "Sample", "new Sample", true, user, null);
        ItemResponseDto expected = ItemResponseDto.builder()
                .id(1L)
                .name("Sample")
                .description("new Sample")
                .available(true)
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();

        assertEquals(expected, ItemMapper.toItemResponseDto(item, null, null, null));
    }

    @Test
    void toItem() {
        Item expected = new Item(null, "Sample", "new Sample", true, null, null);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .name("Sample")
                .description("new Sample")
                .available(true)
                .requestId(null)
                .build();
        assertEquals(expected, ItemMapper.toItem(itemRequestDto, null));
    }

    @Test
    void toItemForRequest_whenItemRequesNotNull() {
        User user = new User(1L, "Name", "mail@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "lolo", user, LocalDateTime.now());
        Item item = new Item(1L, "Sample", "new Sample", true, null, itemRequest);
        ItemForRequest expected = ItemForRequest.builder()
                .id(1L)
                .name("Sample")
                .description("new Sample")
                .requestId(1L)
                .available(true)
                .build();
        assertEquals(expected, ItemMapper.toItemForRequest(item));
    }

    @Test
    void toItemForRequest_whenItemRequesNull() {
        Item item = new Item(1L, "Sample", "new Sample", true, null, null);
        ItemForRequest expected = ItemForRequest.builder()
                .id(1L)
                .name("Sample")
                .description("new Sample")
                .requestId(null)
                .available(true)
                .build();
        assertEquals(expected, ItemMapper.toItemForRequest(item));
    }

    @Test
    void itemToItemUpdateDto_whenRequestIsNull() {
        Item item = new Item(1L, "Sample", "new Sample", true, null, null);
        ItemResponseDto expected = ItemResponseDto.builder()
                .id(1L)
                .description("new Sample")
                .name("Sample")
                .available(true)
                .requestId(null)
                .build();
        assertEquals(expected, ItemMapper.itemToItemUpdateDto(item));
    }

    @Test
    void itemToItemUpdateDto_whenRequestIsNotNull() {
        User user = new User(1L, "Name", "mail@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "lolo", user, LocalDateTime.now());
        Item item = new Item(1L, "Sample", "new Sample", true, null, itemRequest);
        ItemResponseDto expected = ItemResponseDto.builder()
                .id(1L)
                .description("new Sample")
                .name("Sample")
                .available(true)
                .requestId(1L)
                .build();
        assertEquals(expected, ItemMapper.itemToItemUpdateDto(item));
    }
}