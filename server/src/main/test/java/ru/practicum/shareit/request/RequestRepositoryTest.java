package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class RequestRepositoryTest {
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10);
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    long userId;
    long itemId;
    long requestId;

    @BeforeEach
    public void addUserAndItem() {
        User user = new User(null, "test", "test@test.ru");
        Item item = new Item(null, "otvertka", "otvertka", true, user, null);
        ItemRequest itemRequest = new ItemRequest(null, "lolo", user, LocalDateTime.now());
        userRepository.save(user);
        userId = user.getId();
        itemRepository.save(item);
        itemId = item.getId();
        requestRepository.save(itemRequest);
        requestId = itemRequest.getId();
    }

    @AfterEach
    public void deleteAll() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
    }


    @Test
    void findAllByRequester_Id() {
        List<ItemRequest> expected = requestRepository.findAllByRequester_Id(userId);
        assertEquals(expected.size(), 1);
    }

    @Test
    void findAllWithPage() {
        List<ItemRequest> expected = requestRepository.findAllWithPage(userId, PAGE_REQUEST);
        assertEquals(expected.size(), 0);
    }
}