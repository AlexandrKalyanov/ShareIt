package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;


    @Test
    void getUserOrException_whenUserNotFound() {
        assertThrows(ObjectNotFoundException.class, () -> userRepository.getUserOrException(1L));

    }
}