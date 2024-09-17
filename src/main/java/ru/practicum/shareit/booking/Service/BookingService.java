package ru.practicum.shareit.booking.Service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingDtoRequest bookingDtoRequest);

    BookingDto updateBookingApprove(Long userId, Long bookingId, boolean approve);

    Booking getBooking(Long bookingId);

    BookingDto getBookingDtoById(Long userId, Long bookingId);

    List<BookingDto> getAllBookingsFromUser(Long userId, Status state);

    List<BookingDto> getAllBookingFromOwner(Long userId, Status state);
}
