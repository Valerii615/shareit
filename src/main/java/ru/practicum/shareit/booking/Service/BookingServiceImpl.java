package ru.practicum.shareit.booking.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingDbStorage bookingDbStorage;

    @Override
    public BookingDto createBooking(Long userId, BookingDtoRequest bookingDtoRequest) {
        User user = userMapper.toUser(userService.findUserById(userId));
        Item item = itemMapper.toItem(itemService.findItemDtoById(bookingDtoRequest.getItemId()));
        if (!item.getAvailable()) {
            throw new BadRequest("Вещь не доступна для бронирования");
        }
        if (bookingDtoRequest.getStart().equals(bookingDtoRequest.getEnd())) {
            throw new BadRequest("Время начала и окончания не могут быть равны");
        }
        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new BadRequest("Время окончания не может быть раньше времени начала");
        }
        Booking booking = Booking.builder()
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        return bookingMapper.toBookingDto(bookingDbStorage.save(booking));
    }

    @Override
    public Booking getBooking(Long id) {
        Booking booking = bookingDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронироване с " + id + " не найдено"));
        Item item = booking.getItem();
        User booker = booking.getBooker();
        booking.setBooker(booker);
        booking.setItem(item);
        return booking;
    }

    @Override
    public BookingDto updateBookingApprove(Long userId, Long bookingId, boolean approve) {
        User user;
        try {
            user = userMapper.toUser(userService.findUserById(userId));
        } catch (Exception e) {
            throw new BadRequest(e.getMessage());
        }
        Booking booking = getBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(user.getId())) {
            throw new BadRequest("Подтверждать бронирование может только владелец вещи");
        }
        if (approve) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingMapper.toBookingDto(bookingDbStorage.save(booking));
    }

    @Override
    public BookingDto getBookingDtoById(Long userId, Long bookingId) {
        User user = userMapper.toUser(userService.findUserById(userId));
        Booking booking = getBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(user.getId())) {
            if (!booking.getBooker().getId().equals(user.getId())) {
                throw new BadRequest("Пользователь должен быть владельцем вещи или автором бронирования");
            }
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsFromUser(Long userId, Status state) {
        userService.findUserById(userId);
        if (state == Status.ALL) {
            return bookingDbStorage.findByBookerIdOrderByStart(userId).stream()
                    .map(bookingMapper::toBookingDto)
                    .toList();
        } else {
            return bookingDbStorage.findByBookerIdAndStatusOrderByStart(userId, state).stream()
                    .map(bookingMapper::toBookingDto)
                    .toList();
        }
    }

    @Override
    public List<BookingDto> getAllBookingFromOwner(Long userId, Status state) {
        userService.findUserById(userId);
        if (state == Status.ALL) {
            return bookingDbStorage.findByItemOwnerIdOrderByStart(userId).stream()
                    .map(bookingMapper::toBookingDto)
                    .toList();
        } else {
            return bookingDbStorage.findByItemOwnerIdAndStatusOrderByStart(userId, state).stream()
                    .map(bookingMapper::toBookingDto)
                    .toList();
        }
    }


}
