package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Long, Item> itemList;
    private long id;


    @Override
    public Item create(long userId, ItemCreateDto itemCreateDto) {
        Item item = ItemMapper.toItem(generateId(), itemCreateDto, userId);
        itemList.put(this.id, item);
        return itemList.get(this.id);

    }

    @Override
    public Item findByIdWithUser(long itemId, long userId) {
        if (!this.itemList.containsKey(itemId)) {
            throw new ObjectNotFoundException("item not found");
        }
        if (itemList.get(itemId).getOwner() != userId) {
            throw new ObjectNotFoundException("not found");
        }
        return this.itemList.get(itemId);
    }

    @Override
    public Item findById(long itemId) {
        if (!this.itemList.containsKey(itemId)) {
            throw new ObjectNotFoundException("item not found");
        }
        return this.itemList.get(itemId);
    }

    @Override
    public void update(Item item) {
        this.itemList.put(item.getId(), item);
    }

    @Override
    public List<ItemCreateDto> findAllByUser(long userId) {
        return itemList.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemCreateDto> searchItems(String text, long userId) {
        return itemList.values().stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text) || item.getName().toLowerCase().contains(text))
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Long generateId() {
        return ++id;
    }
}
