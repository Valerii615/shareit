package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingDbStorage extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStart(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStart(Long bookerId, Status status);

    @Query("""
            select b from Booking b
            where b.booker.id = ?1
            and b.start < ?2
            and b.end >= ?2
            order by b.start, b.end
            """)
    List<Booking> findByBookerIdAndStartBeforeTimeAfterEnd(Long bookerId, LocalDateTime time);

    @Query("""
            select b from Booking b
            where b.booker.id = ?1
            and b.end < ?2
            order by b.start, b.end
            """)
    List<Booking> findByBookerIdAndTimeAfterEnd(Long bookerId, LocalDateTime time);

    @Query("""
            select b from Booking b
            where b.booker.id = ?1
            and b.start >= ?2
            order by b.start, b.end
            """)
    List<Booking> findByBookerIdAndStartBeforeTime(Long bookerId, LocalDateTime time);

    List<Booking> findByItemOwnerIdOrderByStart(Long ownerId);

    List<Booking> findByItemOwnerIdAndStatusOrderByStart(Long ownerId, Status status);

    @Query("""
            select b from Booking b
            left join b.item i
            where i.owner.id = ?1
            and b.start < ?2
            and b.end >= ?2
            order by b.start, b.end
            """)
    List<Booking> findByOwnerIdAndStartBeforeTimeAfterEnd(Long ownerId, LocalDateTime time);

    @Query("""
            select b from Booking b
            left join b.item i
            where i.owner.id = ?1
            and b.end < ?2
            order by b.start, b.end
            """)
    List<Booking> findByOwnerIdAndTimeAfterEnd(Long ownerId, LocalDateTime time);

    @Query("""
            select b from Booking b
            left join b.item i
            where i.owner.id = ?1
            and b.start >= ?2
            order by b.start, b.end
            """)
    List<Booking> findByOwnerIdAndStartBeforeTime(Long ownerId, LocalDateTime time);

    Booking findByItemIdAndStartBeforeOrderByStart(Long itemId, LocalDateTime timeNow);

    Booking findByItemIdAndStartAfterOrderByStart(Long itemId, LocalDateTime timeNow);

    List<Booking> findByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime timeNow);
}
