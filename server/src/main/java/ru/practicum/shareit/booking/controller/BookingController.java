package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.RequestState;
import ru.practicum.shareit.booking.model.Role;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingServiceImpl;
    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody BookingDtoRequest bookingDtoRequest) {
        return bookingMapper.toBookingDto(bookingServiceImpl.createBooking(userId, bookingDtoRequest));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingApprove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam(value = "approved") boolean approved) {
        return bookingMapper.toBookingDto(bookingServiceImpl.updateBookingApprove(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingServiceImpl.getBookingDtoById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsFromUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(value = "state", defaultValue = "ALL") RequestState state) {
        return bookingServiceImpl.getAllBookingsFromUser(userId, state, Role.USER);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingFromOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(value = "state", defaultValue = "ALL") RequestState state) {
        return bookingServiceImpl.getAllBookingsFromUser(userId, state, Role.OWNER);
    }
}
