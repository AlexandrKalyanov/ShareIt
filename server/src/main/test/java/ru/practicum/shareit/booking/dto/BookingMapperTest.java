package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {

    @Test
    void toBookingDtoResponse() {
        User user = User.builder()
                .id(1L)
                .name("Alex")
                .email("mail@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("desc")
                .request(null)
                .owner(user)
                .available(true)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 12, 12, 12, 12))
                .end(LocalDateTime.of(2024, 12, 12, 19, 12))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(null)
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(UserMapper.toUserDto(user))
                .status(booking.getStatus())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .build();
        assertEquals(bookingDtoResponse, BookingMapper.toBookingDtoResponse(booking));
    }

    @Test
    void toBookingDtoResponse_whenBookingNull() {
        assertNull(BookingMapper.toBookingDtoResponse(null));
    }
}