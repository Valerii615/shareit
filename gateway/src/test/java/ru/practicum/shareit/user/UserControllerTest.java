package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_PREFIX = "/users";
    private static final Long USER_ID = 1L;

    @Test
    @SneakyThrows
    void createUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("user_name")
                .email("www@mail.ru")
                .build();

        mockMvc.perform(post(API_PREFIX)
                .content(objectMapper.writeValueAsString(userDto))
                .header("X-Sharer-User-Id", USER_ID)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void createUserWhenBadEmail() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("user_name")
                .email("wwwmail.ru")
                .build();

        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(userDto))
                        .header("X-Sharer-User-Id", USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
