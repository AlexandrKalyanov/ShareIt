package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserService {

    User create(User user);

    User update(long userId, User user);

    User findById(long userId);

    Collection<User> findAll();

    void deleteById(long userId);
}
