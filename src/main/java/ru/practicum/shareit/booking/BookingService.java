package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto create(long userId, BookingDto bookingDto);

    BookingDto approve(long userId, long bookingId, boolean approved);

    BookingDto findById(long userId, long bookingId);

    Collection<BookingDto> findAllByBooker(long userId, String state);

    Collection<BookingDto> findAllForOwner(long ownerId, String state);
}
