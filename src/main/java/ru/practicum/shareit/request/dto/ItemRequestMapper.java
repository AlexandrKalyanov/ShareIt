package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
@UtilityClass
public class ItemRequestMapper {

    public ItemRequestOutcomeDto toItemRequestOutcomeDto(ItemRequest itemRequest){
        return ItemRequestOutcomeDto.builder()
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .requester(itemRequest.getRequester())
                .description(itemRequest.getDescription()).build();
    }
    public ItemForRequest toItemForRequest(Item item){
        return ItemForRequest.builder()
                .id(item.getId())
                .description(item.getDescription())
                .name(item.getName())
                .requestId(item.getRequest().getId())
                .available(item.getAvailable())
                .build();
    }
}
