package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto create(long userId, BookingDto bookingDto) {
        User booker = getUser(userId);
        Item item = getItem(bookingDto);
        if (!item.getAvailable()){
            throw new ValidationException("Вещь не доступна для бронирования");
        }
        if (item.getOwner().equals(booker.getId())) {
            throw new ObjectNotFoundException("Вы не можете бронировать свою вещь");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Время бронирования указано не корректно");
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);


        return bookingMapper.toBookingDto(bookingRepository.save(booking));

    }

    @Override
    public BookingDto approve(long userId, long bookingId, boolean approved) {
        getUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->new ObjectNotFoundException("booking not found"));
        if (!booking.getItem().getOwner().equals(userId)){
            throw new ObjectNotFoundException("Вы не можете подтверждать чужую вещь");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)){
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (approved){
            booking.setStatus(BookingStatus.APPROVED);
        }
        else booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findById(long userId, long bookingId) {
        getUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("booking not found"));
        if (!booking.getBooker().getId().equals(userId)) {
            if (!booking.getItem().getOwner().equals(userId)) {
                throw new ObjectNotFoundException("Только владлец или создатель запроса может посмотреть запрос");
            }
        }
        return bookingMapper.toBookingDto(bookingRepository.findById(bookingId).get());

    }

    @Override
    public Collection<BookingDto> findAllByBooker(long userId, String state) {
        Collection<Booking> bookings;
        getUser(userId);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
            case "WAITING":
            case "APPROVED":
            case "REJECTED":
            case "CANCELED":
                BookingStatus status = null;
                for (BookingStatus value : BookingStatus.values()) {
                    if (value.name().equals(state)) {
                        status = value;
                    }
                }
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, status);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookerBookings(userId, LocalDateTime.now());
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> findAllForOwner(long ownerId, String state) {
        getUser(ownerId);
        if (itemRepository.findAllByUser(ownerId).isEmpty()) {
            throw new ValidationException("у вас нет вещей");
        }
        Collection<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
            case "WAITING":
            case "APPROVED":
            case "REJECTED":
            case "CANCELED":
                BookingStatus status = null;

                for (BookingStatus value : BookingStatus.values()) {
                    if (value.name().equals(state)) {
                        status = value;
                    }
                }
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, status);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentOwnerBookings(ownerId, LocalDateTime.now());
                break;

            default:
                throw new ValidationException(String.format("Unknown state: %s",state));
        }
        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());

    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    private Item getItem(BookingDto bookingDto) {
        return itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
    }
}
