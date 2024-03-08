package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    User user;

    @BeforeEach
    void addUser() {
       user = new User(null, "Alex", "Alex@mail.ru");
        userRepository.save(user);
    }

    @AfterEach
    void deleteALl() {
        userRepository.deleteAll();
    }

    @Test
    void getUserOrException_whenUserFound() {
        User user1 = userRepository.getUserOrException(1L);
        assertEquals(user, user1);
    }

    @Test
    void getUserOrException_whenUserNotFound() {
        assertThrows(ObjectNotFoundException.class, () -> userRepository.getUserOrException(1L));

    }
}