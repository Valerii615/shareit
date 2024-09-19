package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingDbStorage extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStart(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStart(Long bookerId, Status status);

    List<Booking> findByItemOwnerIdOrderByStart(Long ownerId);

    List<Booking> findByItemOwnerIdAndStatusOrderByStart(Long ownerId, Status status);

    Booking findByItemIdAndStartBeforeOrderByStart(Long itemId, LocalDateTime timeNow);

    Booking findByItemIdAndStartAfterOrderByStart(Long itemId, LocalDateTime timeNow);

    List<Booking> findByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime timeNow);
}
