package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
public class BookingDtoResponse {
    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemResponseDto item;
    private UserDto booker;
    private BookingStatus status;
}
