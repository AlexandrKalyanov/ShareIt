package ru.practicum.shareit.item;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDTOShort;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.GlobalConst.HEADER_USER;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    void create() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .name("Otvertka")
                .description("Otvertka")
                .available(true)
                .build();
        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Otvertka")
                .description("Otvertka")
                .available(true)
                .build();
        when(itemService.create(1L, itemRequestDto)).thenReturn(itemResponseDto);

        String result = mockMvc.perform(post("/items").header(HEADER_USER, 1)
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        assertEquals(objectMapper.writeValueAsString(itemResponseDto), result);


    }

    @SneakyThrows
    @Test
    void update() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Otvertka new")
                .build();
        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Otvertka")
                .description("Otvertka new")
                .available(true)
                .build();
        when(itemService.update(1L, itemRequestDto, 1L)).thenReturn(itemResponseDto);

        String result = mockMvc.perform(patch("/items/1").header(HEADER_USER, 1)
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        assertEquals(objectMapper.writeValueAsString(itemResponseDto), result);
    }

    @SneakyThrows
    @Test
    void findByItemId() {
        long userId = 1L;
        long itemId = 1L;
        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Otvertka")
                .description("Otvertka new")
                .available(true)
                .build();
        when(itemService.findByItemId(userId, itemId)).thenReturn(itemResponseDto);

        String result = mockMvc.perform(get("/items/1").header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService, times(1)).findByItemId(userId, 1L);
        assertEquals(objectMapper.writeValueAsString(itemResponseDto), result);
    }

    @SneakyThrows
    @Test
    void findAllByUser() {
        long userId = 1L;
        mockMvc.perform(get("/items").header(HEADER_USER, userId))
                .andExpect(status().isOk());
        verify(itemService, times(1)).findAllByUser(userId, 0, 10);
    }

    @SneakyThrows
    @Test
    void searchItems() {
        long userId = 1L;
        mockMvc.perform(get("/items/search?text=lolol").header(HEADER_USER, userId))
                .andExpect(status().isOk());
        verify(itemService, times(1)).searchItems(userId, "lolol", 0, 10);

    }

    @SneakyThrows
    @Test
    void createComment() {
        long userId = 1L;
        long itemId = 1L;
        CommentDTOShort commentDTOshort = CommentDTOShort.builder().text("lololo").build();
        mockMvc.perform(post("/items/1/comment").header(HEADER_USER, userId).contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(commentDTOshort)))
                .andExpect(status().isOk());
        verify(itemService, times(1)).createComment(userId, itemId, commentDTOshort);

    }
}