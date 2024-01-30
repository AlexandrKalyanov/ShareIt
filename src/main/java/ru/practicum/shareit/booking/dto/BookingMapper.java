package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Booking;

@Mapper
public interface BookingMapper {

    @Mapping(target = "itemId", ignore = true)
    BookingDtoResponse toBookingDtoResponse(Booking booking);
}
