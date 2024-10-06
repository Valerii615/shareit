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
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingClientTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_PREFIX = "/bookings";
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

}
