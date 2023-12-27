package ru.practicum.shareit.item.storage;


import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {


    Item create(long userId,ItemCreateDto item);

    Item findByIdWithUser(long itemId, long userId);

    Item findById(long itemId);

    void update(Item item);

    List<ItemCreateDto> findAllByUser(long userId);

    List<ItemCreateDto> searchItems(String text, long userId);
}
