package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select i from Booking i " +
            "where i.booker.id = :id " +
            "order by i.start desc",
            countQuery = "select count(i) from Booking i " +
                    "where i.booker.id = :id")
    Collection<Booking> findAllByBookerIdOrderByStartDesc(@Param("id") long id, PageRequest pageRequest);

    @Query(value = "select i from Booking i " +
            "where i.booker.id =:userId and i.status = :status " +
            "order by i.start desc",
            countQuery = "select count(i) from Booking i " +
                    "where i.booker.id = :userId and i.status = :status")
    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(@Param("userId") long userId, @Param("status") BookingStatus status, PageRequest pageRequest);

    @Query(value = "select i from Booking i " +
            "where i.booker.id = :userId and i.end <:now" +
            " order by i.start desc",
            countQuery = "select count(i) from Booking i" +
                    " where i.booker.id = :userId and i.end <:now")
    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(@Param("userId") long userId, @Param("now") LocalDateTime now, PageRequest pageRequest);

    @Query(value = "select i from Booking i " +
            "where i.booker.id = :userId and i.start > :now " +
            "order by i.start desc",
            countQuery = "select count(i) from Booking i " +
                    "where i.booker.id = :userId and i.start > :now")
    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(@Param("userId") long userId, @Param("now") LocalDateTime now, PageRequest pageRequest);

    @Query(value = "select i from Booking i " +
            "where i.booker.id = :userId and i.start < :now and i.end > :now" +
            " order by i.start desc",
            countQuery = "select count(i) from Booking i where i.booker.id = :userId and i.start < :now and i.end > :now")
    Collection<Booking> findCurrentBookerBookings(@Param("userId") long userId, @Param("now") LocalDateTime now, PageRequest pageRequest);

    @Query(value = "select i from Booking i " +
            "where i.item.owner.id = :ownerId " +
            "order by i.start desc",
            countQuery = "select count(i) from Booking i" +
                    " where i.item.owner.id = :ownerId")
    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(@Param("ownerId") long ownerId, PageRequest pageRequest);

    @Query(value = "select i from Booking i " +
            "where i.item.owner.id =:ownerId and i.status = :status " +
            "order by i.start desc",
            countQuery = "select count(i) from Booking i " +
                    "where i.item.owner.id =:ownerId and i.status = :status")
    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(@Param("ownerId") long ownerId, @Param("status") BookingStatus status, PageRequest pageRequest);

    @Query(value = "select i from Booking i " +
            "where i.item.owner.id = :ownerId and i.end < :now " +
            "order by i.start desc",
            countQuery = "select count(i) from Booking i " +
                    "where i.item.owner.id = :ownerId and i.end < :now")
    Collection<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(@Param("ownerId") long ownerId, @Param("now") LocalDateTime now, PageRequest pageRequest);

    @Query(value = "select i from Booking i " +
            "where i.item.owner.id = :ownerId and i.start > :now" +
            " order by i.start desc",
            countQuery = "select count(i) from Booking i " +
                    "where i.item.owner.id = :ownerId and i.start > :now")
    Collection<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(@Param("ownerId") long ownerId, @Param("now") LocalDateTime now, PageRequest pageRequest);

    @Query(value = "select i from Booking i " +
            "where i.item.owner.id = :ownerId and i.start < :now and i.end > :now " +
            "order by i.start desc",
            countQuery = "select count(i) from Booking i " +
                    "where i.item.owner.id = :ownerId and i.start < :now and i.end > :now")
    Collection<Booking> findCurrentOwnerBookings(@Param("ownerId") long ownerId, @Param("now") LocalDateTime now, PageRequest pageRequest);

    @Query(value = "select i from Booking i " +
            "where i.item.id = :itemId  and i.start <= :now and i.status = 'APPROVED' " +
            "order by i.start desc")
    Collection<Booking> findPastBookings(@Param("itemId") long itemId, @Param("now") LocalDateTime now);

    @Query(value = "select i from Booking i " +
            "where i.item.id =:itemId  and i.start > :now and i.status = 'APPROVED' " +
            "order by i.start desc")
    Collection<Booking> findFutureBookings(@Param("itemId") long itemId, @Param("now") LocalDateTime now);

    @Query(value = "select i from Booking i " +
            "where i.item.id in :itemIds and i.item.owner.id = :ownerId and i.start <= :now  and i.status = 'APPROVED' " +
            "order by i.start desc")
    List<Booking> findPastOwnerBookingsAllThings(@Param("itemIds") List<Long> itemIds, @Param("ownerId") long ownerId, @Param("now") LocalDateTime now);

    @Query(value = "select i from Booking i " +
            "where i.item.id in :itemIds and i.item.owner.id = :ownerId and i.start > :now and i.status = 'APPROVED' " +
            "order by i.start desc")
    List<Booking> findFutureOwnerBookingsAllThings(@Param("itemIds") List<Long> itemIds, @Param("ownerId") long ownerId, @Param("now") LocalDateTime now);

    @Query(value = "select count(i) > 0 from Booking i " +
            "where i.item.id =:itemId and i.booker.id = :bookerId and i.end < :now and i.status = 'APPROVED'")
    Boolean findBookings(@Param("itemId") long itemId, @Param("bookerId") long bookerId, @Param("now") LocalDateTime now);
}
