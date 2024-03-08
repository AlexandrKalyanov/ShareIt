package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestOutcomeDto;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.GlobalConst.HEADER_USER;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private RequestService requestService;
    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    void create() {
        ItemRequestIncomeDto itemRequestIncomeDto = ItemRequestIncomeDto.builder()
                .description("text").build();
        ItemRequestOutcomeDto itemRequestOutcomeDto = ItemRequestOutcomeDto.builder()
                .id(1L)
                .build();
        when(requestService.create(1L, itemRequestIncomeDto)).thenReturn(itemRequestOutcomeDto);

        String result = mockMvc.perform(post("/requests").header(HEADER_USER, 1)
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemRequestIncomeDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestOutcomeDto), result);
    }

    @SneakyThrows
    @Test
    void findAllByUserId() {
        ItemRequestOutcomeDto itemRequestOutcomeDto = ItemRequestOutcomeDto.builder()
                .id(1L)
                .build();
        Collection<ItemRequestOutcomeDto> itemRequestOutcomeDtosList = List.of(itemRequestOutcomeDto);
        when(requestService.findAllByUserId(1L)).thenReturn(itemRequestOutcomeDtosList);
        String result = mockMvc.perform(get("/requests").header(HEADER_USER, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestOutcomeDtosList), result);


    }

    @SneakyThrows
    @Test
    void findAll() {
        ItemRequestOutcomeDto itemRequestOutcomeDto = ItemRequestOutcomeDto.builder()
                .id(1L)
                .build();
        Collection<ItemRequestOutcomeDto> itemRequestOutcomeDtosList = List.of(itemRequestOutcomeDto);
        when(requestService.findAll(1L, 0, 10)).thenReturn(itemRequestOutcomeDtosList);
        String result = mockMvc.perform(get("/requests/all?from=0&size=10").header(HEADER_USER, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestOutcomeDtosList), result);
    }
    @SneakyThrows
    @Test
    void findRequest() {
        ItemRequestOutcomeDto itemRequestOutcomeDto = ItemRequestOutcomeDto.builder()
                .id(1L)
                .build();
        when(requestService.findRequest(1L,1L)).thenReturn(itemRequestOutcomeDto);
        String result = mockMvc.perform(get("/requests/1").header(HEADER_USER, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestOutcomeDto), result);

    }
}