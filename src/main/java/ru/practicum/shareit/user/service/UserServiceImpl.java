package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;


    @Override
    public UserDto create(UserDto userDto) {
        User user = userStorage.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        userDto.setId(userId);
        User user = userStorage.update(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
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
