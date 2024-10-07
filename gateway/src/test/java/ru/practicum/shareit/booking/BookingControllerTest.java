package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_PREFIX = "/bookings";
    private static final String API_PREFIX_BAD_STATE = "/bookings?state=bd";
    private static final String API_PREFIX_BAD_FROM = "/bookings?state=all&from=a";
    private static final String API_PREFIX_NEGATIVE_FROM = "/bookings?state=all&from=-1";
    private static final String API_PREFIX_BAD_SIZE = "/bookings?state=all&from=0&size=0";
    private static final Long USER_ID = 1L;

    @Test
    @SneakyThrows
    void bookItem() throws Exception {
        BookItemRequestDto bookingRequestDto = BookItemRequestDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(2))
                .build();

        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void bookItemBadStartTime() throws Exception {
        BookItemRequestDto bookingRequestDto = BookItemRequestDto.builder()
                .start(LocalDateTime.now().minusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(2))
                .build();

        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void bookItemBadEndTime() throws Exception {
        BookItemRequestDto bookingRequestDto = BookItemRequestDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().minusMinutes(2))
                .build();

        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void bookItemBadState() throws Exception {
        try {
            mockMvc.perform(get(API_PREFIX_BAD_STATE)
                            .header("X-Sharer-User-Id", USER_ID)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            assertEquals("Request processing failed: java.lang.IllegalArgumentException: Unknown state: bd", e.getMessage(), "Получено неверное исключение");
        }
    }

    @Test
    @SneakyThrows
    void bookItemBadFrom() throws Exception {
        mockMvc.perform(get(API_PREFIX_BAD_FROM)
                        .header("X-Sharer-User-Id", USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @SneakyThrows
    void bookItemNegativeFrom() throws Exception {
        try {
            mockMvc.perform(get(API_PREFIX_NEGATIVE_FROM)
                            .header("X-Sharer-User-Id", USER_ID)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            assertEquals("Request processing failed: jakarta.validation.ConstraintViolationException: getBookings.from: must be greater than or equal to 0", e.getMessage(), "Получено неверное исключение");
        }
    }

    @Test
    @SneakyThrows
    void bookItemBadSize() throws Exception {
        try {
            mockMvc.perform(get(API_PREFIX_BAD_SIZE)
                            .header("X-Sharer-User-Id", USER_ID)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            assertEquals("Request processing failed: jakarta.validation.ConstraintViolationException: getBookings.size: must be greater than 0", e.getMessage(), "Получено неверное исключение");
        }

    }

}
