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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ItemRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    RequestRepository requestRepository;
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10);

    long userId;
    long itemId;

    @BeforeEach
    void addData() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L).build();
        requestRepository.save(itemRequest);
        User user = new User(null, "test", "test@test.ru");
        Item item = new Item(null, "otvertka", "otvertka", true, user, itemRequest);
        userRepository.save(user);
        userId = user.getId();
        itemRepository.save(item);
        itemId = item.getId();
    }

    @AfterEach
    void deleteData() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
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
        List<Item> actual = itemRepository.searchByRequestIds(List.of(1L));
        assertEquals(1, actual.size());
    }

    @Test
    void getItemOrException_whenItemIsNotFound() {
        assertThrows(ObjectNotFoundException.class, () -> itemRepository.getItemOrException(2L));

    }

}