package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.State;


import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.GlobalConst.HEADER_USER;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void create() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 12, 12, 12, 12);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1L)
                .start(start)
                .end(start.plus(Duration.ofHours(1))).build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .booker(null)
                .itemId(null)
                .build();

        when(bookingService.create(1L, bookingDtoRequest)).thenReturn(bookingDtoResponse);
        String result = mockMvc.perform(post("/bookings").header(HEADER_USER, 1)
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);

    }

    @Test
    void approve() throws Exception {
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .booker(null)
                .itemId(null)
                .build();

        when(bookingService.approve(1L, 1L, true)).thenReturn(bookingDtoResponse);
        String result = mockMvc.perform(patch("/bookings/1?approved=true").header(HEADER_USER, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);
    }

    @Test
    void findById() throws Exception {
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .booker(null)
                .itemId(null)
                .build();

        when(bookingService.findById(1L, 1L)).thenReturn(bookingDtoResponse);
        String result = mockMvc.perform(get("/bookings/1").header(HEADER_USER, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);
    }

    @Test
    void findAllByBooker() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .booker(null)
                .itemId(null)
                .build();
        Collection<BookingDtoResponse> bookingDtoResponseList = List.of(bookingDtoResponse);
        when(bookingService.findAllByBooker(1L, State.ALL, pageRequest)).thenReturn(bookingDtoResponseList);
        String result = mockMvc.perform(get("/bookings?state=ALL&from=0&size=10").header(HEADER_USER, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDtoResponseList), result);
    }

    @Test
    void findAllForOwner() throws Exception {
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .booker(null)
                .itemId(null)
                .build();
        Collection<BookingDtoResponse> bookingDtoResponseList = List.of(bookingDtoResponse);
        when(bookingService.findAllForOwner(1L, State.ALL, any(PageRequest.class))).thenReturn(bookingDtoResponseList);
        String result = mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10").header(HEADER_USER, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDtoResponseList), result);
    }
}