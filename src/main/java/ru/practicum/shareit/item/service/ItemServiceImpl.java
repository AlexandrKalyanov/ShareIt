package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
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
    private final CommentRepository commentRepository;

    @Override
    public ItemResponseDto create(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Item item = ItemMapper.toItem(itemRequestDto);
        item.setOwner(user);
        return ItemMapper.toItemResponseDto(itemRepository.save(item), null, null, null);

    }

    @Override
    public ItemResponseDto update(long userId, ItemRequestDto itemRequestDto, long itemId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Item item = itemRepository.findByIdWithUser(itemId, userId).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
        updateItem(item, itemRequestDto);
        itemRepository.save(item);
        return ItemMapper.itemToItemUpdateDto(item);
    }

    @Override
    public ItemResponseDto findByItemId(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
        BookingForItemDto lastBooking = findLastOwnerBooking(itemId, userId, LocalDateTime.now());
        BookingForItemDto nextBooking = findNextOwnerBooking(itemId, userId, LocalDateTime.now());
        Collection<CommentDTO> commentByItem = commentRepository.findCommentByItem(itemId).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, commentByItem);
        log.debug("responce DTO ->{}", itemResponseDto);
        return itemResponseDto;
    }

    @Override
    public List<ItemResponseDto> findAllByUser(long userId) {
        List<Item> itemsByUser = itemRepository.findAllByUser(userId);
        List<ItemResponseDto> responseList = new ArrayList<>();
        for (Item item : itemsByUser) {
            List<CommentDTO> listComments = commentRepository.findCommentByItem(item.getId()).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
            BookingForItemDto lastBooking = findLastOwnerBooking(item.getId(), userId, LocalDateTime.now());
            BookingForItemDto nextBooking = findNextOwnerBooking(item.getId(), userId, LocalDateTime.now());
            ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, listComments);
            responseList.add(itemResponseDto);
        }
        return responseList.stream().sorted(Comparator.comparing(ItemResponseDto::getId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchItems(long userId, String text) {
        String textLowRegist = text.toLowerCase();
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItems(textLowRegist).stream()
                .map(ItemMapper::itemToItemUpdateDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO createComment(long userId, long itemId, CommentDTOShort commentDTOshort) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
        Collection<Booking> bookings = bookingRepository.findBookings(itemId, userId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь не может комментировать вещь");
        }
        Comment comment = commentRepository.save(new Comment(null, commentDTOshort.getText(), item, user, LocalDateTime.now()));
        return CommentMapper.toCommentDto(comment);
    }

    private void updateItem(Item item, ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getAvailable() != null) {
            item.setAvailable(itemRequestDto.getAvailable());
        }
        if (itemRequestDto.getName() != null) {
            item.setName(itemRequestDto.getName());
        }
        if (itemRequestDto.getDescription() != null) {
            item.setDescription(itemRequestDto.getDescription());
        }
    }

    private BookingForItemDto findLastOwnerBooking(Long itemId, Long userId, LocalDateTime now) {
        return bookingRepository.findPastOwnerBookings(itemId, userId, now)
                .stream()
                .max(Comparator.comparing(Booking::getEnd))
                .map(booking -> new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId()))
                .orElse(null);
    }

    private BookingForItemDto findNextOwnerBooking(Long itemId, Long userId, LocalDateTime now) {
        return bookingRepository.findFutureOwnerBookings(itemId, userId, now)
                .stream()
                .min(Comparator.comparing(Booking::getStart))
                .map(booking -> new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId()))
                .orElse(null);
    }
}
