package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingServiceImpl;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        return bookingServiceImpl.createBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingApprove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam(value = "approved") boolean approved) {
        return bookingServiceImpl.updateBookingApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingServiceImpl.getBookingDtoById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsFromUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(value = "state", defaultValue = "ALL") Status state) {
        return bookingServiceImpl.getAllBookingsFromUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingFromOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(value = "state", defaultValue = "ALL") Status state) {
        return bookingServiceImpl.getAllBookingFromOwner(userId, state);
    }
}
