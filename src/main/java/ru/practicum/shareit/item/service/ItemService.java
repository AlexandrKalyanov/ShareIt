package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.CommentDTOShort;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto create(long userId, ItemRequestDto itemRequestDto);

    ItemResponseDto update(long userId, ItemRequestDto itemRequestDto, long itemId);

    ItemResponseDto findByItemId(long userId, long itemId);

    List<ItemResponseDto> findAllByUser(long userId);

    List<ItemResponseDto> searchItems(long userId, String text);

    CommentDTO createComment(long userId, long itemId, CommentDTOShort commentDTOshort);
}
