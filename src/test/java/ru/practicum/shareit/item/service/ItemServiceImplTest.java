package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
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
    void findByItemId() {
    }

    @Test
    void findAllByUser() {
    }

    @Test
    void searchItems() {
    }

    @Test
    void createComment() {
    }
}