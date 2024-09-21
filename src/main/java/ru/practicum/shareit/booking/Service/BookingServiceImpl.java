package ru.practicum.shareit.booking.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Role;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingDbStorage;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingDbStorage bookingDbStorage;

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingDtoRequest bookingDtoRequest) {
        log.info("Create booking");
        User user = userMapper.toUser(userService.findUserById(userId));
        Item item = itemMapper.toItem(itemService.findItemDtoById(bookingDtoRequest.getItemId()));
        if (!item.getAvailable()) {
            log.error("The item is not available for booking");
            throw new BadRequest("The item is not available for booking");
        }
        if (bookingDtoRequest.getStart().equals(bookingDtoRequest.getEnd())) {
            log.error("The start and end times cannot be equal");
            throw new BadRequest("The start and end times cannot be equal");
        }
        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            log.error("The end time cannot be earlier than the start time");
            throw new BadRequest("The end time cannot be earlier than the start time");
        }
        Booking booking = Booking.builder()
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        BookingDto bookingDto = bookingMapper.toBookingDto(bookingDbStorage.save(booking));
        log.info("Booking created {}", bookingDto);
        return bookingDto;
    }

    @Override
    public Booking getBooking(Long id) {
        log.info("Get booking");
        Booking booking = bookingDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking with " + id + " was not found"));
        Item item = booking.getItem();
        User booker = booking.getBooker();
        booking.setBooker(booker);
        booking.setItem(item);
        log.info("Booking find {}", booking);
        return booking;
    }

    @Override
    @Transactional
    public BookingDto updateBookingApprove(Long userId, Long bookingId, boolean approve) {
        log.info("Update booking approve");
        User user;
        try {
            user = userMapper.toUser(userService.findUserById(userId));
        } catch (Exception e) {
            throw new BadRequest(e.getMessage());
        }
        Booking booking = getBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(user.getId())) {
            log.error("Only the owner of the item can confirm the reservation");
            throw new BadRequest("Only the owner of the item can confirm the reservation");
        }
        if (approve) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        BookingDto bookingDto = bookingMapper.toBookingDto(bookingDbStorage.save(booking));
        log.info("Booking approved {}", bookingDto);
        return bookingDto;
    }

    @Override
    public BookingDto getBookingDtoById(Long userId, Long bookingId) {
        log.info("Get booking dto by id: {}", bookingId);
        User user = userMapper.toUser(userService.findUserById(userId));
        Booking booking = getBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(user.getId())) {
            if (!booking.getBooker().getId().equals(user.getId())) {
                log.error("The user must be the owner of the item or the author of the reservation");
                throw new BadRequest("The user must be the owner of the item or the author of the reservation");
            }
        }
        BookingDto bookingDto = bookingMapper.toBookingDto(booking);
        log.info("Booking dto find {}", bookingDto);
        return bookingDto;
    }

    @Override
    public List<BookingDto> getAllBookingsFromUser(Long userId, Status state, Role role) {
        log.info("Get all bookings from {}", role);
        List<BookingDto> bookingDtoList;
        userService.findUserById(userId);
        if (role == Role.USER) {
            if (state == Status.ALL) {
                bookingDtoList = bookingMapper
                        .toBookingDtoList(bookingDbStorage.findByBookerIdOrderByStart(userId));
                log.info("All bookings from user, size: {}", bookingDtoList.size());
            } else {
                bookingDtoList = bookingMapper
                        .toBookingDtoList(bookingDbStorage.findByBookerIdAndStatusOrderByStart(userId, state));
                log.info("bookings from user, size: {}", bookingDtoList.size());
            }
            return bookingDtoList;
        } else if (role == Role.OWNER) {
            if (state == Status.ALL) {
                bookingDtoList = bookingMapper
                        .toBookingDtoList(bookingDbStorage.findByItemOwnerIdOrderByStart(userId));
                log.info("All bookings from owner, size: {}", bookingDtoList.size());
            } else {
                bookingDtoList = bookingMapper
                        .toBookingDtoList(bookingDbStorage.findByItemOwnerIdAndStatusOrderByStart(userId, state));
                log.info("Bookings from owner, size: {}", bookingDtoList.size());
            }
            return bookingDtoList;
        } else {
            throw new ConflictException("An unknown role was obtained");
        }
    }
}
