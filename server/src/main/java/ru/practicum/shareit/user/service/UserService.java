package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto user);

    UserDto update(long userId, UserDto user);

    UserDto findById(long userId);

    Collection<UserDto> findAll();

    void deleteById(long userId);
}
