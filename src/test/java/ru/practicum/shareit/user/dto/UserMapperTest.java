package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;


class UserMapperTest {

    @Test
    void toUserDto() {
        User user = User.builder()
                .id(1L)
                .name("Alex")
                .email("user@gmail.com")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("user@gmail.com")
                .build();
        UserDto userDtoAfter = UserMapper.toUserDto(user);

        assertEquals(userDto, userDtoAfter);
    }

    @Test
    void toUser() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("user@gmail.com")
                .build();
        User user = User.builder()
                .id(1L)
                .name("Alex")
                .email("user@gmail.com")
                .build();
        User user1 = UserMapper.toUser(userDto);
        assertEquals(user1, user);
    }
}