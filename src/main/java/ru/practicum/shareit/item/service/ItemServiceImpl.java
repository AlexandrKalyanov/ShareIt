package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;


import java.util.Collections;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemCreateDto create(long userId, ItemCreateDto itemCreateDto) {
        userStorage.findById(userId);
        Item item = itemStorage.create(userId, itemCreateDto);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemUpdateDto update(long userId, ItemUpdateDto itemUpdateDto, long itemId) {
        /*
          Проверяем есть ли такой поьзователь в системе
         */
        userStorage.findById(userId);
        /*
          Находим предмет, который хотим изменить
         */
        Item item = itemStorage.findByIdWithUser(itemId, userId);
         /*
          Обновляем поля, которые не null
         */
        updateItem(item, itemUpdateDto);
         /*
         Сохраняем измения
         */
        itemStorage.update(item);

        return ItemMapper.itemToItemUpdateDto(item);
    }

    @Override
    public ItemCreateDto findByItemId(long userId, long itemId) {
        Item item = itemStorage.findById(itemId);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemCreateDto> findAllByUser(long userId) {
        userStorage.findById(userId);
        return itemStorage.findAllByUser(userId);
    }

    @Override
    public List<ItemCreateDto> searchItems(long userId, String text) {
        userStorage.findById(userId);
        if (text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.searchItems(text.toLowerCase(), userId);
    }

    private void updateItem(Item item, ItemUpdateDto itemUpdateDto) {
        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }
    }
}
