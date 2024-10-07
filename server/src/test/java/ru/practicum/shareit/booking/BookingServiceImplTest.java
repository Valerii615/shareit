package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.RequestState;
import ru.practicum.shareit.booking.model.Role;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor
public class BookingServiceImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;


    @Test
    @DirtiesContext
    public void createBooking() {
        User user1 = userService.createUser(new UserDto(null, "user1", "www1@mail.com"));
        User user2 = userService.createUser(new UserDto(null, "user2", "www2@mail.com"));
        Item item1 = itemService.createItem(user1.getId(), new ItemDto(null, "item1", "des1", true, null));
        Item item2 = itemService.createItem(user2.getId(), new ItemDto(null, "item2", "des2", true, null));

        Booking booking1 = bookingService.createBooking(user2.getId(), new BookingDtoRequest(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2), item1.getId()));
        Booking booking2 = bookingService.createBooking(user1.getId(), new BookingDtoRequest(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2), item2.getId()));

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking bookingDb1 = query.setParameter("id", booking1.getId()).getSingleResult();
        Booking bookingDb2 = query.setParameter("id", booking2.getId()).getSingleResult();

        assertThat(bookingDb1, notNullValue());
        assertThat(bookingDb2, notNullValue());

        assertThat(bookingDb1.getStart(), equalTo(booking1.getStart()));
        assertThat(bookingDb1.getEnd(), equalTo(booking1.getEnd()));

        assertThat(bookingDb2.getStart(), equalTo(booking2.getStart()));
        assertThat(bookingDb2.getEnd(), equalTo(booking2.getEnd()));
    }

    @Test
    @DirtiesContext
    public void getBooking() {
        User user1 = userService.createUser(new UserDto(null, "user1", "www1@mail.com"));
        User user2 = userService.createUser(new UserDto(null, "user2", "www2@mail.com"));
        Item item1 = itemService.createItem(user1.getId(), new ItemDto(null, "item1", "des1", true, null));
        Item item2 = itemService.createItem(user2.getId(), new ItemDto(null, "item2", "des2", true, null));
        Booking booking1 = bookingService.createBooking(user2.getId(), new BookingDtoRequest(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2), item1.getId()));
        Booking booking2 = bookingService.createBooking(user1.getId(), new BookingDtoRequest(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2), item2.getId()));

        Booking bookingDb1 = bookingService.getBooking(booking1.getId());
        Booking bookingDb2 = bookingService.getBooking(booking2.getId());

        assertThat(bookingDb1, equalTo(booking1));
        assertThat(bookingDb2, equalTo(booking2));

        assertThat(bookingDb1.getStart(), equalTo(booking1.getStart()));
        assertThat(bookingDb1.getEnd(), equalTo(booking1.getEnd()));

        assertThat(bookingDb2.getStart(), equalTo(booking2.getStart()));
        assertThat(bookingDb2.getEnd(), equalTo(booking2.getEnd()));
    }

    @Test
    @DirtiesContext
    public void updateBookingApprove() {
        User user1 = userService.createUser(new UserDto(null, "user1", "www1@mail.com"));
        User user2 = userService.createUser(new UserDto(null, "user2", "www2@mail.com"));
        Item item1 = itemService.createItem(user1.getId(), new ItemDto(null, "item1", "des1", true, null));

        Booking booking1 = bookingService.createBooking(user2.getId(), new BookingDtoRequest(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2), item1.getId()));
        assertThat(booking1.getStatus(), equalTo(Status.WAITING));

        Booking bookingUp = bookingService.updateBookingApprove(user1.getId(), booking1.getId(), true);
        assertThat(bookingUp.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    @DirtiesContext
    public void getAllBookingsFromUser() {
        User user1 = userService.createUser(new UserDto(null, "user1", "www1@mail.com"));
        User user2 = userService.createUser(new UserDto(null, "user2", "www2@mail.com"));
        Item item1 = itemService.createItem(user2.getId(), new ItemDto(null, "item1", "des1", true, null));
        Item item2 = itemService.createItem(user2.getId(), new ItemDto(null, "item2", "des2", true, null));

        bookingService.createBooking(user1.getId(), new BookingDtoRequest(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2), item1.getId()));
        bookingService.createBooking(user1.getId(), new BookingDtoRequest(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2), item2.getId()));

        List<BookingDto> bookings = bookingService.getAllBookingsFromUser(user1.getId(), RequestState.ALL, Role.USER);
        assertEquals(2, bookings.size(), "Получена неверная длинна списка бронирования");
    }
}
