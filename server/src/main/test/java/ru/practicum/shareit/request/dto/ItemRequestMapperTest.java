package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    @Test
    void toItemRequestOutcomeDto() {
        User user = User.builder()
                .id(1L)
                .name("Alex")
                .email("mail@mail.ru")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("lolol")
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .build();

        ItemRequestOutcomeDto itemRequestOutcomeDto = ItemRequestOutcomeDto
                .builder()
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .requester(itemRequest.getRequester())
                .description(itemRequest.getDescription()).build();

        assertEquals(itemRequestOutcomeDto, ItemRequestMapper.toItemRequestOutcomeDto(itemRequest));
    }

    @Test
    void toItemForRequest() {
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("desc")
                .request(new ItemRequest(1L, null, null, null))
                .owner(null)
                .available(true)
                .build();
        ItemForRequest itemForRequest = ItemForRequest.builder()
                .id(item.getId())
                .description(item.getDescription())
                .name(item.getName())
                .requestId(item.getRequest().getId())
                .available(item.getAvailable())
                .build();

        assertEquals(itemForRequest, ItemRequestMapper.toItemForRequest(item));
    }
}