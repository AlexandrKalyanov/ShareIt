package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;


    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User update(long userId, User user) {
        return userStorage.update(userId, user);
    }

    @Override
    public User findById(long userId) {
        return userStorage.findById(userId);
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public void deleteById(long userId) {
        userStorage.deleteById(userId);
    }
}
