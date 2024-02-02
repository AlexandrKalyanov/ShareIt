package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
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


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemResponseDto create(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.getUserOrException(userId);
        Item item = ItemMapper.toItem(itemRequestDto);
        item.setOwner(user);
        return ItemMapper.toItemResponseDto(itemRepository.save(item), null, null, null);

    }

    @Override
    public ItemResponseDto update(long userId, ItemRequestDto itemRequestDto, long itemId) {
        userRepository.getUserOrException(userId);
        Item item = itemRepository.findByIdWithUser(itemId, userId).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
        updateItem(item, itemRequestDto);
        itemRepository.save(item);
        return ItemMapper.itemToItemUpdateDto(item);
    }

    @Override
    public ItemResponseDto findByItemId(long userId, long itemId) {
        Item item = itemRepository.getItemOrException(itemId);
        BookingForItemDto lastBooking = findLastOwnerBooking(itemId, userId, LocalDateTime.now());
        BookingForItemDto nextBooking = findNextOwnerBooking(itemId, userId, LocalDateTime.now());
        Collection<CommentDTO> commentByItem = commentRepository.findCommentByItem(itemId).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        return ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, commentByItem);
    }

    @Override
    public List<ItemResponseDto> findAllByUser(long userId) {
        List<ItemResponseDto> responseList = new ArrayList<>();
        List<Item> itemsByUser = itemRepository.findAllByUser(userId);
        List<Long> itemsIdsList = itemsByUser.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> allBookingLast = bookingRepository.f1indPastOwnerBookings(itemsIdsList, userId, LocalDateTime.now());
        List<Booking> allBookingNext = bookingRepository.f1indFutureOwnerBookings(itemsIdsList, userId, LocalDateTime.now());

        for (Item item : itemsByUser) {
            BookingForItemDto lastBooking = allBookingLast.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .max(Comparator.comparing(Booking::getEnd))
                    .map(booking -> new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId()))
                    .orElse(null);
            BookingForItemDto nextBooking = allBookingNext.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .min(Comparator.comparing(Booking::getStart))
                    .map(booking -> new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId()))
                    .orElse(null);
            ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, Collections.emptyList());
            responseList.add(itemResponseDto);
        }
        Map<Long, ArrayList<Comment>> collect = commentRepository.findAllCommentsInListItemsIds(itemsByUser.stream().map(Item::getId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(), Collectors.toCollection(ArrayList::new)));

        for (ItemResponseDto itemResponseDto : responseList) {
            Long itemResponseDtoId = itemResponseDto.getId();
            if (collect.containsKey(itemResponseDtoId)) {
                List<CommentDTO> comments = collect.get(itemResponseDtoId).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
                itemResponseDto.setComments(comments);
            }
        }
        return responseList;
    }

    @Override
    public List<ItemResponseDto> searchItems(long userId, String text) {
        String textLowRegister = text.toLowerCase();
        userRepository.getUserOrException(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItems(textLowRegister).stream()
                .map(ItemMapper::itemToItemUpdateDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO createComment(long userId, long itemId, CommentDTOShort commentDTOshort) {
        User user = userRepository.getUserOrException(userId);
        Item item = itemRepository.getItemOrException(itemId);
        Boolean isBookings = bookingRepository.findBookings(itemId, userId, LocalDateTime.now());
        if (!isBookings) {
            throw new ValidationException("Пользователь не может комментировать вещь");
        }
        Comment comment = commentRepository.save(new Comment(null, commentDTOshort.getText(), item, user, LocalDateTime.now()));
        return CommentMapper.toCommentDto(comment);
    }

    private void updateItem(Item item, ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getAvailable() != null) {
            item.setAvailable(itemRequestDto.getAvailable());
        }
        if (itemRequestDto.getName() != null && !itemRequestDto.getName().isBlank()) {
            item.setName(itemRequestDto.getName());
        }
        if (itemRequestDto.getDescription() != null && !itemRequestDto.getDescription().isBlank()) {
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
