package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

@UtilityClass
public final class BookingMapper {


    public BookingDtoResponse toBookingDtoResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .itemId(null)
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .item(ItemMapper.toItemResponseDto(booking.getItem(), null, null, null))
                .build();
    }

    ;
}
