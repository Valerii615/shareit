package ru.practicum.shareit.booking.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingDbStorage;
import ru.practicum.shareit.exception.BadRequest;
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
        log.info("Create booking request");
        User user = userMapper.toUser(userService.findUserById(userId));
        Item item = itemMapper.toItem(itemService.findItemDtoById(bookingDtoRequest.getItemId()));
        if (!item.getAvailable()) {
            log.error("Вещь не доступна для бронирования");
            throw new BadRequest("Вещь не доступна для бронирования");
        }
        if (bookingDtoRequest.getStart().equals(bookingDtoRequest.getEnd())) {
            log.error("Время начала и окончания не могут быть равны");
            throw new BadRequest("Время начала и окончания не могут быть равны");
        }
        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            log.error("Время окончания не может быть раньше времени начала");
            throw new BadRequest("Время окончания не может быть раньше времени начала");
        }
        Booking booking = Booking.builder()
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        BookingDto bookingDto = bookingMapper.toBookingDto(bookingDbStorage.save(booking));
        log.info("Booking created");
        return bookingDto;
    }

    @Override
    public Booking getBooking(Long id) {
        log.info("Get booking");
        Booking booking = bookingDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронироване с " + id + " не найдено"));
        Item item = booking.getItem();
        User booker = booking.getBooker();
        booking.setBooker(booker);
        booking.setItem(item);
        log.info("Booking find");
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
            log.error("Подтверждать бронирование может только владелец вещи");
            throw new BadRequest("Подтверждать бронирование может только владелец вещи");
        }
        if (approve) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        BookingDto bookingDto = bookingMapper.toBookingDto(bookingDbStorage.save(booking));
        log.info("Booking approved");
        return bookingDto;
    }

    @Override
    public BookingDto getBookingDtoById(Long userId, Long bookingId) {
        log.info("Get booking dto by id");
        User user = userMapper.toUser(userService.findUserById(userId));
        Booking booking = getBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(user.getId())) {
            if (!booking.getBooker().getId().equals(user.getId())) {
                log.error("Пользователь должен быть владельцем вещи или автором бронирования");
                throw new BadRequest("Пользователь должен быть владельцем вещи или автором бронирования");
            }
        }
        BookingDto bookingDto = bookingMapper.toBookingDto(booking);
        log.info("Booking dto find");
        return bookingDto;
    }

    @Override
    public List<BookingDto> getAllBookingsFromUser(Long userId, Status state) {
        log.info("Get all bookings from user");
        userService.findUserById(userId);
        if (state == Status.ALL) {
            List<BookingDto> bookingDtoList = bookingDbStorage.findByBookerIdOrderByStart(userId).stream()
                    .map(bookingMapper::toBookingDto)
                    .toList();
            log.info("All bookings from user");
            return bookingDtoList;
        } else {
            List<BookingDto> bookingDtoList = bookingDbStorage.findByBookerIdAndStatusOrderByStart(userId, state).stream()
                    .map(bookingMapper::toBookingDto)
                    .toList();
            log.info("bookings from user");
            return bookingDtoList;
        }
    }

    @Override
    public List<BookingDto> getAllBookingFromOwner(Long userId, Status state) {
        log.info("Get all bookings from owner");
        userService.findUserById(userId);
        if (state == Status.ALL) {
            List<BookingDto> bookingDtoList = bookingDbStorage.findByItemOwnerIdOrderByStart(userId).stream()
                    .map(bookingMapper::toBookingDto)
                    .toList();
            log.info("All bookings from owner");
            return bookingDtoList;
        } else {
            List<BookingDto> bookingDtoList = bookingDbStorage.findByItemOwnerIdAndStatusOrderByStart(userId, state).stream()
                    .map(bookingMapper::toBookingDto)
                    .toList();
            log.info("bookings from owner");
            return bookingDtoList;
        }
    }
}
