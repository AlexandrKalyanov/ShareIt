package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    void create() {
        UserDto userDto = new UserDto(1L, "name", "name@mail.ru");
        when(userService.create(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(userDto), result);

    }

    @SneakyThrows
    @Test
    void update() {
        long userId = 0L;
        UserDto userToCreate = new UserDto();
        userToCreate.setId(userId);
        UserDto userUpdated = UserDto.builder()
                .email("updated@mail.ru")
                .id(userId)
                .build();
        when(userService.update(userId, userToCreate)).thenReturn(userUpdated);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userUpdated), result);
    }

    @SneakyThrows
    @Test
    void findById() {
        long userId = 0L;
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).findById(userId);
    }

    @SneakyThrows
    @Test
    void findAll() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService, times(1)).findAll();
    }

    @SneakyThrows
    @Test
    void deleteById() {
        long userId = 0L;
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(userId);
    }
}