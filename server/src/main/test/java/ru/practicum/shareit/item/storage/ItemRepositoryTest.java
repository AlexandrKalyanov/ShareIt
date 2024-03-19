package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ItemRepositoryTest {
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10);
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    RequestRepository requestRepository;
    long userId;
    long itemId;

    @BeforeEach
    void addData() {
        User user = new User(null, "test", "test@test.ru");
        userRepository.save(user);
        userId = user.getId();
        Item item = new Item(null, "otvertka", "otvertka", true, user, null);
        itemRepository.save(item);
        itemId = item.getId();
    }

    @AfterEach
    void deleteData() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    void findByIdWithUser() {
        Optional<Item> actual = itemRepository.findByIdWithUser(userId, userId);
        assertTrue(actual.isPresent());
    }

    @Test
    void findAllByUser() {
        List<Item> actual = itemRepository.findAllByUser(userId, PAGE_REQUEST);
        assertEquals(1, actual.size());
    }

    @Test
    void existsByOwnerId() {
        assertTrue(itemRepository.existsByOwnerId(userId));
    }

    @Test
    void searchItems() {
        List<Item> actual = itemRepository.searchItems("otve", PAGE_REQUEST);
        assertEquals(1, actual.size());

    }

    @Test
    void searchByRequestIds() {
        User user2 = new User(null, "test2", "test2@test.ru");
        userRepository.save(user2);
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user2)
                .description("lolo")
                .created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);
        Item item = new Item(null, "otvertka", "otvertka", true, user2, itemRequest);
        itemRepository.save(item);
        List<Item> actual = itemRepository.searchByRequestIds(List.of(1L));
        assertEquals(1, actual.size());
    }

    @Test
    void getItemOrException_whenItemIsNotFound() {
        assertThrows(ObjectNotFoundException.class, () -> itemRepository.getItemOrException(2L));

    }

}