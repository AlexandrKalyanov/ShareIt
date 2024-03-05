package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;



    @Test
    void create_whenInvoked_withoutItemRequest_thenReturnedItem() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .name("otvertka")
                .description("New otvertka")
                .available(true)
                .build();
        Item item = Item.builder()
                .id(1L)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .request(null)
                .available(itemRequestDto.getAvailable())
                .owner(user)
                .build();
        ItemResponseDto expectedItem = ItemMapper.toItemResponseDto(item, null, null, null);
        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(item);
        ItemResponseDto actualItem = itemService.create(userId, itemRequestDto);
        assertEquals(actualItem, expectedItem);
        verify(userRepository).getUserOrException(userId);

    }

    @Test
    void create_whenInvoked_withItemRequest_thenReturnedItem() {
        long userId1 = 1L;
        User user = User.builder()
                .id(userId1)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("mail2@mail.ru")
                .name("Ivan")
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .name("otvertka")
                .description("New otvertka")
                .requestId(1L)
                .available(true)
                .build();
        ItemRequest requestItem = ItemRequest.builder()
                .id(1L)
                .description("otvertka")
                .created(LocalDateTime.now())
                .requester(user2)
                .build();
        Item item = Item.builder()
                .id(1L)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .owner(user)
                .build();
        ItemResponseDto expectedItem = ItemMapper.toItemResponseDto(item, null, null, null);
        expectedItem.setRequestId(requestItem.getId());

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(requestRepository.findById(any())).thenReturn(Optional.of(requestItem));
        when(itemRepository.save(any())).thenReturn(item);
        ItemResponseDto actualItem = itemService.create(userId1, itemRequestDto);
        assertEquals(expectedItem, actualItem);
        verify(userRepository).getUserOrException(userId1);
    }

    @Test
    void create_whenInvoked_withItemRequest_thenReturnedException() {
        long userId1 = 1L;
        User user = User.builder()
                .id(userId1)
                .email("mail@mail.ru")
                .name("Alex")
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .name("otvertka")
                .description("New otvertka")
                .requestId(1L)
                .available(true)
                .build();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(requestRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.create(userId1, itemRequestDto));
        verify(userRepository).getUserOrException(userId1);

    }

    @Test
    void update_whenUpdateAvailable_thenReturnedItem() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .name("otvertka")
                .description("New otvertka")
                .requestId(1L)
                .available(false)
                .build();
        Item itemBeforeUpdate = Item.builder()
                .id(1L)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(true)
                .owner(user)
                .build();
        Item itemAfterUpdate = Item.builder()
                .id(1L)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(false)
                .owner(user)
                .build();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.findByIdWithUser(1L, userId)).thenReturn(Optional.of(itemBeforeUpdate));
        when(itemRepository.save(itemBeforeUpdate)).thenReturn(itemBeforeUpdate);
        ItemResponseDto expected = ItemMapper.toItemResponseDto(itemAfterUpdate, null, null, null);
        ItemResponseDto actual = itemService.update(userId, itemRequestDto, 1L);
        assertEquals(expected, actual);
    }

    @Test
    void update_whenUpdateName_thenReturnedItem() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .name("otvertka update")
                .description("New otvertka")
                .requestId(1L)
                .available(true)
                .build();
        Item itemBeforeUpdate = Item.builder()
                .id(1L)
                .name("otvertka")
                .description(itemRequestDto.getDescription())
                .available(true)
                .owner(user)
                .build();
        Item itemAfterUpdate = Item.builder()
                .id(1L)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(true)
                .owner(user)
                .build();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.findByIdWithUser(1L, userId)).thenReturn(Optional.of(itemBeforeUpdate));
        when(itemRepository.save(itemBeforeUpdate)).thenReturn(itemBeforeUpdate);
        ItemResponseDto expected = ItemMapper.toItemResponseDto(itemAfterUpdate, null, null, null);
        ItemResponseDto actual = itemService.update(userId, itemRequestDto, 1L);
        assertEquals(expected, actual);
    }

    @Test
    void update_whenUpdateDescription_thenReturnedItem() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .name("otvertka update")
                .description("update New otvertka")
                .requestId(1L)
                .available(true)
                .build();
        Item itemBeforeUpdate = Item.builder()
                .id(1L)
                .name(itemRequestDto.getName())
                .description("New otvertka")
                .available(true)
                .owner(user)
                .build();
        Item itemAfterUpdate = Item.builder()
                .id(1L)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(true)
                .owner(user)
                .build();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.findByIdWithUser(1L, userId)).thenReturn(Optional.of(itemBeforeUpdate));
        when(itemRepository.save(itemBeforeUpdate)).thenReturn(itemBeforeUpdate);
        ItemResponseDto expected = ItemMapper.toItemResponseDto(itemAfterUpdate, null, null, null);
        ItemResponseDto actual = itemService.update(userId, itemRequestDto, 1L);
        assertEquals(expected, actual);
    }

    @Test
    void findByItemId_whenUserIsOwner_returnedItem() {
        long itemId = 1L;
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("nick@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("Otvertka")
                .description("new otvertka")
                .available(true)
                .owner(user)
                .build();

        Comment comment = new Comment(
                1L,
                "Text",
                item,
                user,
                LocalDateTime.now());
        CommentDTO commentDTO = CommentMapper.toCommentDto(comment);
        Booking last = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 3, 4, 16, 11).minus(Duration.ofHours(1)))
                .end(LocalDateTime.of(2024, 3, 4, 16, 11).minus(Duration.ofMinutes(15)))
                .item(item)
                .status(BookingStatus.APPROVED)
                .booker(user2)
                .build();
        Booking next = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 3, 4, 16, 11).plus(Duration.ofHours(1)))
                .end(LocalDateTime.of(2024, 3, 4, 16, 11).plus(Duration.ofMinutes(75)))
                .item(item)
                .status(BookingStatus.APPROVED)
                .booker(user2)
                .build();
        BookingForItemDto bookingForItemDtoLast = BookingForItemDto.builder()
                .id(last.getId())
                .start(last.getStart())
                .end(last.getEnd())
                .bookerId(last.getBooker().getId())
                .build();
        BookingForItemDto bookingForItemDtoNext = BookingForItemDto.builder()
                .id(next.getId())
                .start(next.getStart())
                .end(next.getEnd())
                .bookerId(next.getBooker().getId())
                .build();
        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.getItemOrException(itemId)).thenReturn(item);
        when(commentRepository.findCommentByItem(itemId)).thenReturn(List.of(comment));
        lenient().when(bookingRepository.findPastBookings(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(last));
        lenient().when(bookingRepository.findFutureBookings(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(next));

        ItemResponseDto expected = ItemMapper.toItemResponseDto(item, bookingForItemDtoLast, bookingForItemDtoNext, List.of(commentDTO));
        ItemResponseDto actual = itemService.findByItemId(userId, itemId);
        assertEquals(expected, actual);

        verify(userRepository).getUserOrException(anyLong());
        verify(itemRepository).getItemOrException(itemId);
        verify(commentRepository).findCommentByItem(itemId);
        verify(bookingRepository).findFutureBookings(anyLong(), any(LocalDateTime.class));
        verify(bookingRepository).findPastBookings(anyLong(), any(LocalDateTime.class));

    }

    @Test
    void findByItemId_whenUserIsNotOwner_returnedItem() {
        long itemId = 1L;
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("nick@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("Otvertka")
                .description("new otvertka")
                .available(true)
                .owner(user2)
                .build();

        Comment comment = new Comment(
                1L,
                "Text",
                item,
                user,
                LocalDateTime.now());
        CommentDTO commentDTO = CommentMapper.toCommentDto(comment);

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.getItemOrException(itemId)).thenReturn(item);
        when(commentRepository.findCommentByItem(itemId)).thenReturn(List.of(comment));

        ItemResponseDto expected = ItemMapper.toItemResponseDto(item, null, null, List.of(commentDTO));
        ItemResponseDto actual = itemService.findByItemId(userId, itemId);
        assertEquals(expected, actual);

        verify(userRepository).getUserOrException(anyLong());
        verify(itemRepository).getItemOrException(itemId);
        verify(commentRepository).findCommentByItem(itemId);


    }

    @Test
    void findAllByUser_whenBookingIsExsist_thenReturnedItemList() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("mail2@mail.ru")
                .name("Ivan")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("Otvertka")
                .description("new otvertka")
                .available(true)
                .owner(user)
                .build();
        Booking last = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 3, 4, 16, 11).minus(Duration.ofHours(1)))
                .end(LocalDateTime.of(2024, 3, 4, 16, 11).minus(Duration.ofMinutes(15)))
                .item(item)
                .status(BookingStatus.APPROVED)
                .booker(user2)
                .build();
        Booking next = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 3, 4, 16, 11).plus(Duration.ofHours(1)))
                .end(LocalDateTime.of(2024, 3, 4, 16, 11).plus(Duration.ofMinutes(75)))
                .item(item)
                .status(BookingStatus.APPROVED)
                .booker(user2)
                .build();
        Comment comment = Comment.builder()
                .text("comment")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        when(itemRepository.findAllByUser(anyLong(),any(PageRequest.class))).thenReturn(List.of(item));
        when(bookingRepository.findPastOwnerBookingsAllThings(anyList(),anyLong(),any(LocalDateTime.class))).thenReturn(List.of(last));
        when(bookingRepository.findFutureOwnerBookingsAllThings(anyList(),anyLong(),any(LocalDateTime.class))).thenReturn(List.of(next));
        when(commentRepository.findAllCommentsInListItemsIds(anyList())).thenReturn(List.of(comment));

        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item,
                new BookingForItemDto(last.getId(),last.getStart(),last.getEnd(),last.getBooker().getId()),
                        new BookingForItemDto(next.getId(),next.getStart(),next.getEnd(),next.getBooker().getId()),
                        List.of(CommentMapper.toCommentDto(comment)));

        List<ItemResponseDto> expected = List.of(itemResponseDto);
        List<ItemResponseDto> actual = itemService.findAllByUser(userId, 0, 10);
        assertEquals(expected,actual);

    }
    @Test
    void findAllByUser_whenBookingNon_thenReturnedItemList() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("Otvertka")
                .description("new otvertka")
                .available(true)
                .owner(user)
                .build();
        Comment comment = Comment.builder()
                .text("comment")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        when(itemRepository.findAllByUser(anyLong(),any(PageRequest.class))).thenReturn(List.of(item));
        when(bookingRepository.findPastOwnerBookingsAllThings(anyList(),anyLong(),any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(bookingRepository.findFutureOwnerBookingsAllThings(anyList(),anyLong(),any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(commentRepository.findAllCommentsInListItemsIds(anyList())).thenReturn(List.of(comment));

        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item,
                null,
                null,
                List.of(CommentMapper.toCommentDto(comment)));

        List<ItemResponseDto> expected = List.of(itemResponseDto);
        List<ItemResponseDto> actual = itemService.findAllByUser(userId, 0, 10);
        assertEquals(expected,actual);

    }

    @Test
    void searchItems_whenTextIsBlank_thenReturnedEmptyList() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        List<ItemResponseDto> actual = itemService.searchItems(userId, "", from, size);

        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    void searchItems_whenTextIsExist_thenReturnedEmptyList() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        String text = "TEXT";
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("Otvertka")
                .description("new otvertka")
                .available(true)
                .owner(user)
                .build();
        ItemResponseDto itemResponseDto = ItemMapper.itemToItemUpdateDto(item);
        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.searchItems(anyString(), any(PageRequest.class))).thenReturn(List.of(item));
        List<ItemResponseDto> actual = itemService.searchItems(userId, text, from, size);

        assertEquals(List.of(itemResponseDto), actual);
        verify(userRepository).getUserOrException(anyLong());
        verify(itemRepository).searchItems(anyString(),any(PageRequest.class));
    }

    @Test
    void createComment_whenBookingIsTrue_thenReturnedComment() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("Otvertka")
                .description("new otvertka")
                .available(true)
                .owner(user)
                .build();
        CommentDTOShort commentDTOShort = CommentDTOShort.builder()
                .text("comment")
                .build();
        Comment comment = Comment.builder()
                .text("comment")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        when(userRepository.getUserOrException(userId)).thenReturn(user);
        when(itemRepository.getItemOrException(item.getId())).thenReturn(item);
        when(bookingRepository.findBookings(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDTO expected = CommentMapper.toCommentDto(comment);
        CommentDTO actual = itemService.createComment(userId, item.getId(), commentDTOShort);

        assertEquals(expected, actual);
        verify(userRepository).getUserOrException(userId);
        verify(itemRepository).getItemOrException(item.getId());
        verify(bookingRepository).findBookings(anyLong(), anyLong(), any(LocalDateTime.class));
        verify(commentRepository).save(any(Comment.class));

    }

    @Test
    void createComment_whenBookingIsFalse_thenReturnedException() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("Otvertka")
                .description("new otvertka")
                .available(true)
                .owner(user)
                .build();
        CommentDTOShort commentDTOShort = CommentDTOShort.builder()
                .text("comment")
                .build();

        when(userRepository.getUserOrException(userId)).thenReturn(user);
        when(itemRepository.getItemOrException(item.getId())).thenReturn(item);
        when(bookingRepository.findBookings(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(false);

        assertThrows(ValidationException.class, () -> itemService.createComment(userId, item.getId(), commentDTOShort));
        verify(userRepository).getUserOrException(userId);
        verify(itemRepository).getItemOrException(item.getId());
        verify(bookingRepository).findBookings(anyLong(), anyLong(), any(LocalDateTime.class));
    }


}