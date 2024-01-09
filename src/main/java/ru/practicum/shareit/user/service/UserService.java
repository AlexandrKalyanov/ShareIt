package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto user);

    UserDto update(long userId, UserDto user);

    User findById(long userId);

    Collection<User> findAll();

    void deleteById(long userId);
}
