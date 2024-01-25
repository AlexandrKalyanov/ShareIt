package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemCreateDto create(long userId, ItemCreateDto itemCreateDto) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Item item = ItemMapper.toItem(itemCreateDto);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.save(item), null, null);

    }

    @Override
    public ItemUpdateDto update(long userId, ItemUpdateDto itemUpdateDto, long itemId) {
        /*
          Проверяем есть ли такой поьзователь в системе
         */
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        /*
          Находим предмет, который хотим изменить
         */
        //Item item = itemStorage.findByIdWithUser(itemId, userId);
        Item item = itemRepository.findByIdWithUser(itemId, userId).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
         /*
          Обновляем поля, которые не null
         */
        updateItem(item, itemUpdateDto);
         /*
         Сохраняем измения
         */
        //itemStorage.update(item);
        itemRepository.save(item);

        return ItemMapper.itemToItemUpdateDto(item);
    }

    @Override
    public ItemCreateDto findByItemId(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
        BookingForItemDto lastBooking = findlastOwnerBooking(itemId,userId, LocalDateTime.now());
        BookingForItemDto nextBooking = findNextOwnerBooking(itemId,userId, LocalDateTime.now());
        ItemCreateDto itemCreateDto = ItemMapper.toItemDto(item, lastBooking, nextBooking);
        log.debug("responce DTO ->{}", itemCreateDto);
        return itemCreateDto;
    }

    @Override
    public List<ItemCreateDto> findAllByUser(long userId) {
        List<Item> itemsByUser = itemRepository.findAllByUser(userId);
        List<ItemCreateDto> responseList = new ArrayList<>();
        for (Item item : itemsByUser) {
            ItemCreateDto itemCreateDto = ItemMapper.toItemDto(item, findlastOwnerBooking(item.getId(), userId, LocalDateTime.now()), findNextOwnerBooking(item.getId(), userId, LocalDateTime.now()));
            responseList.add(itemCreateDto);
        }
        return responseList.stream().sorted(Comparator.comparing(ItemCreateDto::getId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemUpdateDto> searchItems(long userId, String text) {
        String textLowRegist = text.toLowerCase();
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        if (text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItems(textLowRegist).stream()
                .map(ItemMapper::itemToItemUpdateDto)
                .collect(Collectors.toList());
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

    private BookingForItemDto findlastOwnerBooking(Long itemId, Long userId, LocalDateTime now) {
        return bookingRepository.findPastOwnerBookings(itemId, userId, LocalDateTime.now())
                .stream()
                .min(Comparator.comparing(Booking::getEnd))
                .map(booking -> new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId()))
                .orElse(null);
    }

    private BookingForItemDto findNextOwnerBooking(Long itemId, Long userId, LocalDateTime now) {
        return bookingRepository.findFutureOwnerBookings(itemId, userId, LocalDateTime.now())
                .stream()
                .min(Comparator.comparing(Booking::getStart))
                .map(booking -> new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId()))
                .orElse(null);
    }

    private BookingForItemDto findlastBooking(Long itemId, LocalDateTime now) {
        return bookingRepository.findPastBookings(itemId, LocalDateTime.now())
                .stream()
                .max(Comparator.comparing(Booking::getEnd))
                .map(booking -> new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId()))
                .orElse(null);
    }

    private BookingForItemDto findNextBooking(Long itemId, LocalDateTime now) {
        return bookingRepository.findFutureBookings(itemId, LocalDateTime.now())
                .stream()
                .min(Comparator.comparing(Booking::getStart))
                .map(booking -> new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId()))
                .orElse(null);
    }
}
