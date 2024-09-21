package ru.practicum.shareit.booking.Service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.RequestState;
import ru.practicum.shareit.booking.model.Role;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, BookingDtoRequest bookingDtoRequest);

    Booking updateBookingApprove(Long userId, Long bookingId, boolean approve);

    Booking getBooking(Long bookingId);

    BookingDto getBookingDtoById(Long userId, Long bookingId);

    List<BookingDto> getAllBookingsFromUser(Long userId, RequestState state, Role role);
}
