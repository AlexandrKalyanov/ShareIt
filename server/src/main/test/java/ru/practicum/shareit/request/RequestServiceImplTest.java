package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutcomeDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private RequestServiceImpl requestService;


    @Test
    void create_thenReturnedItemRequest() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        ItemRequestIncomeDto itemRequestIncomeDto = ItemRequestIncomeDto.builder()
                .description("lolo").build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .build();
        ItemRequestOutcomeDto expected = ItemRequestOutcomeDto.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .items(null)
                .build();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        ItemRequestOutcomeDto actual = requestService.create(1L, itemRequestIncomeDto);
        assertEquals(expected, actual);
    }

    @Test
    void findAllByUserId_whenItemsIsNotEmpty_whenReturnedListRequests() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(itemRequest)
                .available(true)
                .owner(user)
                .build();
        ItemForRequest itemForRequest = ItemMapper.toItemForRequest(item);
        List<ItemRequest> itemRequestList = List.of(itemRequest);
        ItemRequestOutcomeDto expected = ItemRequestOutcomeDto.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .items(List.of(itemForRequest))
                .build();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(requestRepository.findAllByRequester_Id(anyLong())).thenReturn(itemRequestList);
        when(itemRepository.searchByRequestIds(anyList())).thenReturn(List.of(item));
        Collection<ItemRequestOutcomeDto> actual = requestService.findAllByUserId(1L);
        assertEquals(List.of(expected), actual);

    }

    @Test
    void findAllByUserId_whenItemsIsEmpty_whenReturnedListRequests() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .build();
        List<ItemRequest> itemRequestList = List.of(itemRequest);
        ItemRequestOutcomeDto expected = ItemRequestOutcomeDto.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .items(Collections.emptyList())
                .build();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(requestRepository.findAllByRequester_Id(anyLong())).thenReturn(itemRequestList);
        when(itemRepository.searchByRequestIds(anyList())).thenReturn(Collections.emptyList());

        Collection<ItemRequestOutcomeDto> actual = requestService.findAllByUserId(1L);
        assertEquals(List.of(expected), actual);

    }

    @Test
    void findAll_whenItemsIsNotEmpty_thenReturnedListRequests() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(itemRequest)
                .available(true)
                .owner(user)
                .build();
        ItemForRequest itemForRequest = ItemMapper.toItemForRequest(item);
        List<ItemRequest> itemRequestList = List.of(itemRequest);
        ItemRequestOutcomeDto expected = ItemRequestOutcomeDto.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .items(List.of(itemForRequest))
                .build();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(requestRepository.findAllWithPage(anyLong(), any(PageRequest.class))).thenReturn(itemRequestList);
        when(itemRepository.searchByRequestIds(anyList())).thenReturn(List.of(item));
        Collection<ItemRequestOutcomeDto> actual = requestService.findAll(1L, 0, 10);
        assertEquals(List.of(expected), actual);
    }

    @Test
    void findAll_whenItemsIsEmpty_thenReturnedListRequests() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .build();

        List<ItemRequest> itemRequestList = List.of(itemRequest);
        ItemRequestOutcomeDto expected = ItemRequestOutcomeDto.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .items(null)
                .build();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(requestRepository.findAllWithPage(anyLong(), any(PageRequest.class))).thenReturn(itemRequestList);
        when(itemRepository.searchByRequestIds(anyList())).thenReturn(Collections.emptyList());
        Collection<ItemRequestOutcomeDto> actual = requestService.findAll(1L, 0, 10);
        assertEquals(List.of(expected), actual);
    }

    @Test
    void findRequest_whenItemsIsNotEmpty_thenReturnedRequests() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(itemRequest)
                .available(true)
                .owner(user)
                .build();
        List<ItemForRequest> itemRequestList = List.of(ItemRequestMapper.toItemForRequest(item));
        ItemRequestOutcomeDto expected = ItemRequestOutcomeDto.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .items(itemRequestList)
                .build();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.searchByRequestIds(anyList())).thenReturn(List.of(item));
        ItemRequestOutcomeDto actual = requestService.findRequest(1L, 1L);
        assertEquals(expected, actual);
    }

    @Test
    void findRequest_whenItemsIsEmpty_thenReturnedRequests() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .build();
        List<ItemForRequest> itemRequestList = Collections.emptyList();
        ItemRequestOutcomeDto expected = ItemRequestOutcomeDto.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.of(2024, 12, 12, 12, 12))
                .description("lolo")
                .items(itemRequestList)
                .build();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.searchByRequestIds(anyList())).thenReturn(Collections.emptyList());
        ItemRequestOutcomeDto actual = requestService.findRequest(1L, 1L);
        assertEquals(expected, actual);
    }
}