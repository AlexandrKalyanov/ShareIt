package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDtoResponse create(long userId, BookingDtoRequest bookingDtoRequest) {
        User booker = userRepository.getUserOrException(userId);
        Item item = itemRepository.getItemOrException(bookingDtoRequest.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования");
        }
        if (item.getOwner().getId().equals(booker.getId())) {
            throw new ObjectNotFoundException("Вы не можете бронировать свою вещь");
        }
        Booking booking = getBooking(bookingDtoRequest, booker, item);
        Booking bookingSave = bookingRepository.save(booking);
        return bookingMapper.toBookingDtoResponse(bookingSave);

    }


    @Override
    public BookingDtoResponse approve(long userId, long bookingId, boolean approved) {
        userRepository.getUserOrException(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("booking not found"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Вы не можете подтверждать чужую вещь");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return bookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse findById(long userId, long bookingId) {
        userRepository.getUserOrException(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("booking not found"));
        if (!booking.getBooker().getId().equals(userId)) {
            if (!booking.getItem().getOwner().getId().equals(userId)) {
                throw new ObjectNotFoundException("Только владлец или создатель запроса может посмотреть запрос");
            }
        }
        return bookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public Collection<BookingDtoResponse> findAllByBooker(long userId, State state, PageRequest pageRequest) {
        Collection<Booking> bookings;
        userRepository.getUserOrException(userId);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest).stream().map(bookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            case WAITING:
            case APPROVED:
            case REJECTED:
            case CANCELED:
                BookingStatus status = null;
                for (BookingStatus value : BookingStatus.values()) {
                    if (value.name().equals(state.name())) {
                        status = value;
                        break;
                    }
                }
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, status, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookerBookings(userId, LocalDateTime.now(), pageRequest);
                break;
            default:
                throw new ValidationException(String.format("Unknown state: %s", state));
        }
        return bookings.stream().map(bookingMapper::toBookingDtoResponse).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDtoResponse> findAllForOwner(long ownerId, State state, PageRequest pageRequest) {
        userRepository.getUserOrException(ownerId);
        if (!itemRepository.existsByOwnerId(ownerId)) {
            return Collections.emptyList();
        }
        Collection<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageRequest);
                return bookings.stream().map(bookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            case WAITING:
            case APPROVED:
            case REJECTED:
            case CANCELED:
                BookingStatus status = null;

                for (BookingStatus value : BookingStatus.values()) {
                    if (value.name().equals(state.name())) {
                        status = value;
                    }
                }
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, status, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentOwnerBookings(ownerId, LocalDateTime.now(), pageRequest);
                break;

            default:
                throw new ValidationException(String.format("Unknown state: %s", state));
        }
        return bookings.stream().map(bookingMapper::toBookingDtoResponse).collect(Collectors.toList());

    }

    private Booking getBooking(BookingDtoRequest bookingDtoRequest, User booker, Item item) {
        return Booking.builder()
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

    }
}
