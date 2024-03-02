package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
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
    private final RequestRepository requestRepository;

    @Override
    public ItemResponseDto create(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.getUserOrException(userId);
        ItemRequest itemRequest = null;
        if (itemRequestDto.getRequestId() != null) {
            Optional<ItemRequest> request = requestRepository.findById(itemRequestDto.getRequestId());
            if (request.isEmpty()) {
                throw new ObjectNotFoundException("Request id not found");
            }
            itemRequest = request.get();
        }
        Item item = ItemMapper.toItem(itemRequestDto, itemRequest);
        item.setOwner(user);
        Item itemSave = itemRepository.save(item);
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(itemSave, null, null, null);
        if (item.getRequest() == null) {
            itemResponseDto.setRequestId(null);
        } else itemResponseDto.setRequestId(item.getRequest().getId());
        return itemResponseDto;
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
        Collection<CommentDTO> commentByItem = commentRepository.findCommentByItem(itemId).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        if (item.getOwner().getId() == userId) {
            BookingForItemDto lastBooking = findLastOwnerBooking(itemId, LocalDateTime.now());
            BookingForItemDto nextBooking = findNextOwnerBooking(itemId, LocalDateTime.now());
            return ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, commentByItem);
        } else return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(commentByItem)
                .build();
    }

    @Override
    public List<ItemResponseDto> findAllByUser(long userId, int from, int size) {
        List<ItemResponseDto> responseList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Item> itemsByUser = itemRepository.findAllByUser(userId, pageRequest);
        List<Long> itemsIdsList = itemsByUser.stream().map(Item::getId).collect(Collectors.toList());
        Map<Long, List<Booking>> allBookingMapLast = bookingRepository.findPastOwnerBookingsAllThings(itemsIdsList, userId, LocalDateTime.now())
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.toList()));
        Map<Long, List<Booking>> allBookingMapNext = bookingRepository.findFutureOwnerBookingsAllThings(itemsIdsList, userId, LocalDateTime.now())
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.toList()));

        for (Item item : itemsByUser) {
            BookingForItemDto lastBooking;
            BookingForItemDto nextBooking;
            if (allBookingMapLast.containsKey(item.getId())) {
                lastBooking = allBookingMapLast.get(item.getId())
                        .stream()
                        .filter(booking -> booking.getItem().getId().equals(item.getId()))
                        .max(Comparator.comparing(Booking::getEnd))
                        .map(booking -> new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId()))
                        .orElse(null);
            } else {
                lastBooking = null;
            }
            if (allBookingMapNext.containsKey(item.getId())) {
                nextBooking = allBookingMapNext.get(item.getId())
                        .stream()
                        .filter(booking -> booking.getItem().getId().equals(item.getId()))
                        .min(Comparator.comparing(Booking::getStart))
                        .map(booking -> new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId()))
                        .orElse(null);

            } else {
                nextBooking = null;
            }
            ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, Collections.emptyList());
            responseList.add(itemResponseDto);
        }
        Map<Long, List<Comment>> collect = commentRepository.findAllCommentsInListItemsIds(itemsByUser.stream().map(Item::getId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(), Collectors.toCollection(ArrayList::new)));

        for (ItemResponseDto itemResponseDto : responseList) {
            Long itemResponseDtoId = itemResponseDto.getId();
            List<CommentDTO> comments = collect.getOrDefault(itemResponseDtoId, Collections.emptyList()).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
            itemResponseDto.setComments(comments);
        }
        return responseList;
    }

    @Override
    public List<ItemResponseDto> searchItems(long userId, String text, int from, int size) {
        String textLowRegister = text.toLowerCase();
        userRepository.getUserOrException(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemRepository.searchItems(textLowRegister, pageRequest).stream()
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

    private BookingForItemDto findLastOwnerBooking(Long itemId, LocalDateTime now) {
        return bookingRepository.findPastBookings(itemId, now)
                .stream()
                .max(Comparator.comparing(Booking::getEnd))
                .map(booking -> new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId()))
                .orElse(null);
    }

    private BookingForItemDto findNextOwnerBooking(Long itemId, LocalDateTime now) {
        return bookingRepository.findFutureBookings(itemId, now)
                .stream()
                .min(Comparator.comparing(Booking::getStart))
                .map(booking -> new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getBooker().getId()))
                .orElse(null);
    }
}
