package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select i from Booking i where i.booker.id =?1 order by i.start desc")
    Collection<Booking> findAllByBookerIdOrderByStartDesc(long id);
    @Query("select i from Booking i where i.booker.id =?1 and i.status = ?2 order by i.start desc")
    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, BookingStatus status);

    @Query(value = "SELECT * FROM BOOKINGS WHERE BOOKER_ID = ?1 AND END_DATE < ?2 ORDER BY START_DATE DESC ", nativeQuery = true)
    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime now);

    @Query(value = "SELECT * FROM BOOKINGS WHERE BOOKER_ID = ?1 AND START_DATE > ?2 ORDER BY START_DATE DESC ", nativeQuery = true)
    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime now);

    @Query(value = "SELECT * FROM BOOKINGS WHERE BOOKER_ID = ?1 AND START_DATE < ?2 AND END_DATE > ?2 ORDER BY START_DATE DESC", nativeQuery = true)
    Collection<Booking> findCurrentBookerBookings(long userId, LocalDateTime now);

    @Query(value = "SELECT * FROM BOOKINGS JOIN ITEMS I on I.ID = BOOKINGS.ITEM_ID WHERE I.OWNER_ID = ?1 ORDER BY START_DATE DESC", nativeQuery = true)
    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);
    @Query("select i from Booking i where i.item.owner =?1 and i.status = ?2 order by i.start desc")
    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    @Query(value = "SELECT * FROM BOOKINGS JOIN ITEMS I on I.ID = BOOKINGS.ITEM_ID WHERE I.OWNER_ID = ?1 AND END_DATE < ?2 ORDER BY START_DATE DESC", nativeQuery = true)
    Collection<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime now);

    @Query(value = "SELECT * FROM BOOKINGS JOIN ITEMS I on I.ID = BOOKINGS.ITEM_ID WHERE I.OWNER_ID = ?1 AND START_DATE > ?2 ORDER BY START_DATE DESC", nativeQuery = true)
    Collection<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime now);
    @Query(value = "SELECT * FROM BOOKINGS JOIN ITEMS I on I.ID = BOOKINGS.ITEM_ID WHERE I.OWNER_ID = ?1 AND START_DATE < ?2 AND END_DATE > ?2 ORDER BY START_DATE DESC", nativeQuery = true)
    Collection<Booking> findCurrentOwnerBookings(long ownerId, LocalDateTime now);
    @Query(value = "select i from Booking i where i.item.id = ?1 and i.item.owner = ?2 and i.end < ?3 order by i.start desc")
    Collection<Booking> findPastOwnerBookings(long itemId, long ownerId, LocalDateTime now);
    @Query(value = "select i from Booking i where i.item.id =?1 and i.item.owner = ?2 and i.start > ?3 order by i.start desc")
    Collection<Booking> findFutureOwnerBookings(long itemId, long ownerId, LocalDateTime now);

    @Query(value = "select i from Booking i where i.item.id = ?1 and i.end < ?2 order by i.start desc")
    Collection<Booking> findPastBookings(long itemId, LocalDateTime now);

    @Query(value = "select i from Booking i where i.item.id =?1 and i.start > ?2 order by i.start desc")
    Collection<Booking> findFutureBookings(long itemId, LocalDateTime now);
}
