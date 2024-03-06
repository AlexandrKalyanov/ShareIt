package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10);
    long userId;
    long itemId;
    long bookingId;



    @BeforeEach
   public void addUserAndItem() {
        User user = new User(null, "test", "test@test.ru");
        Item item = new Item(null, "otvertka", "otvertka", true, user, null);
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minus(Duration.ofMinutes(10)))
                .end(LocalDateTime.now().plus(Duration.ofHours(1)))
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        userRepository.save(user);
        userId = user.getId();
        itemRepository.save(item);
        itemId = item.getId();
        bookingRepository.save(booking);
        bookingId = booking.getId();
    }
    @AfterEach
    public void deleteBookings() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }


    @Test
    void findAllByBookerIdOrderByStartDesc() {
        Collection<Booking> expected = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, PAGE_REQUEST);
        assertEquals(expected.size(), 1);

    }


    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        Collection<Booking> expected = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,BookingStatus.APPROVED, PAGE_REQUEST);
        assertEquals(expected.size(), 1);
    }


    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc() {
        Collection<Booking> expected = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId,LocalDateTime.now(), PAGE_REQUEST);
        assertEquals(expected.size(), 0);

    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
        Collection<Booking> expected = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId,LocalDateTime.now(),PAGE_REQUEST);
        assertEquals(expected.size(), 0);
    }

    @Test
    void findCurrentBookerBookings() {
        Collection<Booking> expected = bookingRepository.findCurrentBookerBookings(userId,LocalDateTime.now(),PAGE_REQUEST);
        assertEquals(expected.size(), 1);
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
        Collection<Booking> expected = bookingRepository.findCurrentBookerBookings(userId,LocalDateTime.now(),PAGE_REQUEST);
        assertEquals(expected.size(), 1);
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDesc() {
        Collection<Booking> expected = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId,BookingStatus.APPROVED,PAGE_REQUEST);
        assertEquals(expected.size(), 1);
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        Collection<Booking> expected = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId,LocalDateTime.now(),PAGE_REQUEST);
        assertEquals(expected.size(), 0);

    }

    @Test
    void findAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        Collection<Booking> expected = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId,LocalDateTime.now(),PAGE_REQUEST);
        assertEquals(expected.size(), 0);
    }

    @Test
    void findCurrentOwnerBookings() {
        Collection<Booking> expected = bookingRepository.findCurrentOwnerBookings(userId,LocalDateTime.now(),PAGE_REQUEST);
        assertEquals(expected.size(), 1);
    }

    @Test
    void findPastBookings() {
        Collection<Booking> expected = bookingRepository.findPastBookings(itemId,LocalDateTime.now());
        assertEquals(expected.size(), 1);
    }

    @Test
    void findFutureBookings() {
        Collection<Booking> expected = bookingRepository.findFutureBookings(itemId,LocalDateTime.now());
        assertEquals(expected.size(), 0);
    }

    @Test
    void findPastOwnerBookingsAllThings() {
        Collection<Booking> expected = bookingRepository.findPastOwnerBookingsAllThings(List.of(itemId),userId,LocalDateTime.now());
        assertEquals(expected.size(), 1);
    }

    @Test
    void findFutureOwnerBookingsAllThings() {
        Collection<Booking> expected = bookingRepository.findPastOwnerBookingsAllThings(List.of(itemId),userId,LocalDateTime.now());
        assertEquals(expected.size(), 1);
    }

    @Test
    void findBookings() {
        boolean expected = bookingRepository.findBookings(itemId,userId,LocalDateTime.now());
        assertFalse(expected);
    }
}