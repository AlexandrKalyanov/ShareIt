package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select i from Booking i where i.booker.id =?1 order by i.start desc")
    Collection<Booking> findAllByBookerIdOrderByStartDesc(long id);

    @Query("select i from Booking i where i.booker.id =?1 and i.status = ?2 order by i.start desc")
    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, BookingStatus status);

    @Query(value = "select i from Booking i where i.booker.id = ?1 and i.end <?2 order by i.start desc")
    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime now);

    @Query(value = "select i from Booking i where i.booker.id = ?1 and i.start > ?2 order by i.start desc")
    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime now);

    @Query(value = "select i from Booking i where i.booker.id = ?1 and i.start <?2 and i.end > ?2 order by i.start desc")
    Collection<Booking> findCurrentBookerBookings(long userId, LocalDateTime now);

    @Query(value = "select i from Booking i where i.item.owner.id = ?1 order by i.start desc")
    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    @Query("select i from Booking i where i.item.owner.id =?1 and i.status = ?2 order by i.start desc")
    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    @Query(value = "select i from Booking i where i.item.owner.id = ?1 and i.end < ?2 order by i.start desc ")
    Collection<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime now);

    @Query(value = "select i from Booking i where i.item.owner.id = ?1 and i.start > ?2 order by i.start desc")
    Collection<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime now);

    @Query(value = "select i from Booking i where i.item.owner.id = ?1 and i.start < ?2 and i.end > ?2 order by i.start desc")
    Collection<Booking> findCurrentOwnerBookings(long ownerId, LocalDateTime now);

    @Query(value = "select i from Booking i where i.item.id = ?1 and i.item.owner.id = ?2 and i.start <= ?3  and i.status = 'APPROVED' order by i.start desc")
    Collection<Booking> findPastOwnerBookings(long itemId, long ownerId, LocalDateTime now);

    @Query(value = "select i from Booking i where i.item.id =?1 and i.item.owner.id = ?2 and i.start > ?3 and i.status = 'APPROVED' order by i.start desc")
    Collection<Booking> findFutureOwnerBookings(long itemId, long ownerId, LocalDateTime now);

    @Query(value = "select i from Booking i where i.item.id in ?1 and i.item.owner.id = ?2 and i.start <= ?3  and i.status = 'APPROVED' order by i.start desc")
    List<Booking> findPastOwnerBookingsAllThings(List<Long> itemIds, long ownerId, LocalDateTime now);

    @Query(value = "select i from Booking i where i.item.id in ?1 and i.item.owner.id = ?2 and i.start > ?3 and i.status = 'APPROVED' order by i.start desc")
    List<Booking> findFutureOwnerBookingsAllThings(List<Long> itemIds, long ownerId, LocalDateTime now);

    @Query(value = "select count(i) > 0 from Booking i where i.item.id =?1 and i.booker.id = ?2 and i.end < ?3 and i.status = 'APPROVED'")
    Boolean findBookings(long itemId, long bookerId, LocalDateTime now);
}
