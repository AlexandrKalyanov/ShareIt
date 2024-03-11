package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void create_whenItemIsAvailable_thenReturnedBooking() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user)
                .build();
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .itemId(item.getId())
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user2))
                .status(BookingStatus.WAITING)
                .build();
        when(userRepository.getUserOrException(anyLong())).thenReturn(user2);
        when(itemRepository.getItemOrException(anyLong())).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingDtoResponse(any(Booking.class))).thenReturn(bookingDtoResponse);
        BookingDtoResponse actual = bookingService.create(userId2, bookingDtoRequest);

        assertEquals(bookingDtoResponse, actual);
    }

    @Test
    void create_whenItemIsNotAvailable_thenReturnedValidationException() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(false)
                .owner(user)
                .build();
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .itemId(item.getId())
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .build();
        when(userRepository.getUserOrException(anyLong())).thenReturn(user2);
        when(itemRepository.getItemOrException(anyLong())).thenReturn(item);

        assertThrows(ValidationException.class, () -> bookingService.create(userId2, bookingDtoRequest));
    }

    @Test
    void create_whenItemIsOwn_thenReturnedObjectNotFoundException() {
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .itemId(item.getId())
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .build();
        when(userRepository.getUserOrException(anyLong())).thenReturn(user2);
        when(itemRepository.getItemOrException(anyLong())).thenReturn(item);

        assertThrows(ObjectNotFoundException.class, () -> bookingService.create(userId2, bookingDtoRequest));
    }

    @Test
    void approve_whenApprovedIsTrue_thenReturnedBookingWithApproved() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .itemId(item.getId())
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        Booking bookingAfterSave = Booking.builder()
                .id(1L)
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user2))
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingAfterSave);
        when(bookingMapper.toBookingDtoResponse(any(Booking.class))).thenReturn(bookingDtoResponse);
        BookingDtoResponse actual = bookingService.approve(userId2, 1L, true);

        assertEquals(bookingDtoResponse, actual);

    }

    @Test
    void approve_whenApprovedIsFalse_thenReturnedBookingWithApproved() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .itemId(item.getId())
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        Booking bookingAfterSave = Booking.builder()
                .id(1L)
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(user2)
                .status(BookingStatus.REJECTED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user2))
                .status(BookingStatus.REJECTED)
                .build();
        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingAfterSave);
        when(bookingMapper.toBookingDtoResponse(any(Booking.class))).thenReturn(bookingDtoResponse);
        BookingDtoResponse actual = bookingService.approve(userId2, 1L, false);

        assertEquals(bookingDtoResponse, actual);
    }

    @Test
    void approve_whenTryApprovedNotOwnItem_thenReturnedObjectNotFoundException() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user)
                .build();
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .itemId(item.getId())
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ObjectNotFoundException.class, () -> bookingService.approve(userId2, 1L, true));
    }

    @Test
    void approve_whenApprovedIsDone_thenReturnedValidationException() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .itemId(item.getId())
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approve(userId2, 1L, true));
    }

    @Test
    void findById_whenDataIsCorrect_thenReturnedBooking() {
        long userId = 1L;
        long bookingId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDtoResponse(any(Booking.class))).thenReturn(bookingDtoResponse);

        BookingDtoResponse actual = bookingService.findById(userId, bookingId);

        assertEquals(bookingDtoResponse, actual);

    }

    @Test
    void findById_whenDataIsNotCorrect_thenReturnedObjectNotFoundException() {
        long userId = 1L;
        long bookingId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .item(item)
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ObjectNotFoundException.class, () -> bookingService.findById(userId, bookingId));

    }

    @Test
    void findAllByBooker_whenStateIsApproved_thenReturnedListOfBooking() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();
        Collection<BookingDtoResponse> expected = List.of(bookingDtoResponse);

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.APPROVED, pageRequest)).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDtoResponse(booking)).thenReturn(bookingDtoResponse);


        Collection<BookingDtoResponse> actual = bookingService.findAllByBooker(userId, State.APPROVED, pageRequest);

        assertEquals(expected, actual);
    }

    @Test
    void findAllByBooker_whenStateIsAll_thenReturnedListOfBooking() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();
        Collection<BookingDtoResponse> expected = List.of(bookingDtoResponse);

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest)).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDtoResponse(booking)).thenReturn(bookingDtoResponse);


        Collection<BookingDtoResponse> actual = bookingService.findAllByBooker(userId, State.ALL, pageRequest);

        assertEquals(expected, actual);
    }

    @Test
    void findAllByBooker_whenStateIsPast_thenReturnedListOfBooking() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();
        Collection<BookingDtoResponse> expected = List.of(bookingDtoResponse);

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDtoResponse(booking)).thenReturn(bookingDtoResponse);


        Collection<BookingDtoResponse> actual = bookingService.findAllByBooker(userId, State.PAST, pageRequest);

        assertEquals(expected, actual);
    }

    @Test
    void findAllByBooker_whenStateIsFuture_thenReturnedListOfBooking() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();
        Collection<BookingDtoResponse> expected = List.of(bookingDtoResponse);

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDtoResponse(booking)).thenReturn(bookingDtoResponse);


        Collection<BookingDtoResponse> actual = bookingService.findAllByBooker(userId, State.FUTURE, pageRequest);

        assertEquals(expected, actual);
    }

    @Test
    void findAllByBooker_whenStateIsCurrent_thenReturnedListOfBooking() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();
        Collection<BookingDtoResponse> expected = List.of(bookingDtoResponse);

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(bookingRepository.findCurrentBookerBookings(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDtoResponse(booking)).thenReturn(bookingDtoResponse);

        Collection<BookingDtoResponse> actual = bookingService.findAllByBooker(userId, State.CURRENT, pageRequest);
        assertEquals(expected, actual);
        verify(userRepository).getUserOrException(anyLong());
        verify(bookingRepository).findCurrentBookerBookings(anyLong(), any(LocalDateTime.class), any(PageRequest.class));
    }

    @Test
    void findAllByBooker_whenStateIsUnknown_thenReturnedListOfBooking() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        assertThrows(ValidationException.class, () -> bookingService.findAllByBooker(userId, State.UNSUPPORTED_STATUS, pageRequest));
        verify(userRepository).getUserOrException(anyLong());
    }

    @Test
    void findAllForOwner_whenStateIsAll_whenReturnedBooking() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();
        Collection<BookingDtoResponse> expected = List.of(bookingDtoResponse);

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.existsByOwnerId(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageRequest)).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDtoResponse(booking)).thenReturn(bookingDtoResponse);


        Collection<BookingDtoResponse> actual = bookingService.findAllForOwner(userId, State.ALL, pageRequest);

        assertEquals(expected, actual);
    }

    @Test
    void findAllForOwner_whenItemIsEmpty_whenReturnedEmptyList() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        Collection<BookingDtoResponse> expected = Collections.emptyList();

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.existsByOwnerId(anyLong())).thenReturn(false);


        Collection<BookingDtoResponse> actual = bookingService.findAllForOwner(userId, State.ALL, pageRequest);

        assertEquals(expected, actual);
    }

    @Test
    void findAllForOwner_whenStateIsWaiting_whenReturnedBooking() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();
        Collection<BookingDtoResponse> expected = List.of(bookingDtoResponse);

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.existsByOwnerId(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDtoResponse(booking)).thenReturn(bookingDtoResponse);


        Collection<BookingDtoResponse> actual = bookingService.findAllForOwner(userId, State.WAITING, pageRequest);

        assertEquals(expected, actual);
    }

    @Test
    void findAllForOwner_whenStateIsPast_whenReturnedBooking() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();
        Collection<BookingDtoResponse> expected = List.of(bookingDtoResponse);

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.existsByOwnerId(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDtoResponse(booking)).thenReturn(bookingDtoResponse);


        Collection<BookingDtoResponse> actual = bookingService.findAllForOwner(userId, State.PAST, pageRequest);

        assertEquals(expected, actual);
    }

    @Test
    void findAllForOwner_whenStateIsFuture_whenReturnedBooking() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();
        Collection<BookingDtoResponse> expected = List.of(bookingDtoResponse);

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.existsByOwnerId(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDtoResponse(booking)).thenReturn(bookingDtoResponse);


        Collection<BookingDtoResponse> actual = bookingService.findAllForOwner(userId, State.FUTURE, pageRequest);

        assertEquals(expected, actual);
    }

    @Test
    void findAllForOwner_whenStateIsCurrent_whenReturnedBooking() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        long userId2 = 2L;
        User user2 = User.builder()
                .id(userId2)
                .email("booker@mail.ru")
                .name("Nick")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("New otvertka")
                .request(null)
                .available(true)
                .owner(user2)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 10, 11, 20))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemResponseDto(item, null, null, null))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();
        Collection<BookingDtoResponse> expected = List.of(bookingDtoResponse);

        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.existsByOwnerId(anyLong())).thenReturn(true);
        when(bookingRepository.findCurrentOwnerBookings(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDtoResponse(booking)).thenReturn(bookingDtoResponse);


        Collection<BookingDtoResponse> actual = bookingService.findAllForOwner(userId, State.CURRENT, pageRequest);

        assertEquals(expected, actual);
    }

    @Test
    void findAllForOwner_whenStateIsUnknown_whenReturnedBooking() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Alex")
                .build();
        when(userRepository.getUserOrException(anyLong())).thenReturn(user);
        when(itemRepository.existsByOwnerId(anyLong())).thenReturn(true);

        assertThrows(ValidationException.class, () -> bookingService.findAllForOwner(userId, State.UNSUPPORTED_STATUS, pageRequest));
    }
}