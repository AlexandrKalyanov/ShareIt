package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    boolean existsByEmail(String name);

    User update(long userId, User user);

    User findById(long userId);

    Collection<User> findAll();

    void deleteById(long userId);
}
