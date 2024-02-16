package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.State;

import java.util.Collection;

public interface BookingService {
    BookingDtoResponse create(long userId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse approve(long userId, long bookingId, boolean approved);

    BookingDtoResponse findById(long userId, long bookingId);

    Collection<BookingDtoResponse> findAllByBooker(long userId, State state, int from, int size);

    Collection<BookingDtoResponse> findAllForOwner(long ownerId, State state, int from, int size);
}
