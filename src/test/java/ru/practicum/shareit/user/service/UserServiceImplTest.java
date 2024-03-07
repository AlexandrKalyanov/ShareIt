package ru.practicum.shareit.user.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    ArgumentCaptor<User> argumentCaptor;


    @Test
    void create_whenInvoked_thenReturnedUser() {
        User userToSave = new User(null, "Alex", "alex@mail.ru");
        User expectedUser = User.builder().id(1L).name("Alex").email("alex@mail.ru").build();
        when(userRepository.save(userToSave)).thenReturn(expectedUser);

        UserDto actualUser = userService.create(toUserDto(userToSave));

        assertEquals(actualUser, toUserDto(expectedUser));
        verify(userRepository).save(userToSave);
    }

    @Test
    void update_whenNameIsEmpty_returnUser() {
        long userId = 1L;
        UserDto incomeUser = new UserDto();
        incomeUser.setId(userId);
        incomeUser.setEmail("update@mail.ru");
        User oldUser = User.builder()
                .id(userId)
                .name("Alex")
                .email("alex@mail.ru")
                .build();
        User updateUser = User.builder()
                .id(userId)
                .name("Alex")
                .email("update@mail.ru")
                .build();
        when(userRepository.getUserOrException(userId)).thenReturn(oldUser);
        when(userRepository.save(updateUser)).thenReturn(updateUser);


        UserDto actualUser = userService.update(userId, incomeUser);


        assertEquals(actualUser.getName(), oldUser.getName());
        assertEquals(actualUser.getEmail(), updateUser.getEmail());

    }

    @Test
    void update_whenEmailIsEmpty_returnUser() {
        long userId = 1L;
        UserDto incomeUser = new UserDto();
        incomeUser.setId(userId);
        incomeUser.setName("Ivan");


        User oldUser = User.builder()
                .id(userId)
                .name("Alex")
                .email("alex@mail.ru")
                .build();
        User updateUser = User.builder()
                .id(userId)
                .name("Ivan")
                .email("alex@mail.ru")
                .build();
        when(userRepository.getUserOrException(userId)).thenReturn(oldUser);
        when(userRepository.save(updateUser)).thenReturn(updateUser);


        userService.update(userId, incomeUser);

        verify(userRepository).save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();

        assertEquals("Ivan", savedUser.getName());
        assertEquals("alex@mail.ru", savedUser.getEmail());

    }

    @Test
    void findById_whenUserFound_thenReturnedUser() {
        long userId = 0L;
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        User actualUser = toUser(userService.findById(userId));

        assertEquals(actualUser, expectedUser);
        verify(userRepository).findById(userId);
    }

    @Test
    void findById_whenUserNotFound_thenReturnedObjectNotFoundException() {
        long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userService.findById(userId));
        verify(userRepository).findById(userId);

    }


    @Test
    void findAll() {
        List<User> expectedList = Collections.emptyList();
        when(userRepository.findAll()).thenReturn(expectedList);
        List<UserDto> expectedListAfterMapper = expectedList.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        Collection<UserDto> actualUserList = userService.findAll();

        assertEquals(expectedListAfterMapper, actualUserList);
        verify(userRepository).findAll();

    }

    @Test
    void deleteById_test() {
        long userIdToDelete = 1L;
        userService.deleteById(userIdToDelete);
        verify(userRepository).deleteById(userIdToDelete);
    }

}