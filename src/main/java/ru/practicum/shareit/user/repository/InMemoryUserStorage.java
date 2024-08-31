package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserRowMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryUserStorage {
    private final UserRowMapper userRowMapper = new UserRowMapper();

    private final Map<Long, User> users = new HashMap<>();
    private Long countId = 0L;

    public UserDto addUser(UserDto userDto) {
        log.info("Adding new user: {}", userDto);
        validateUserEmail(userDto.getEmail());
        User user = User.builder()
                .id(++countId)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
        users.put(user.getId(), user);
        UserDto userDto1 = userRowMapper.toUserDto(users.get(user.getId()));
        log.info("Added user: {}", userDto1);
        return userDto1;
    }

    public UserDto getUser(Long id) {
        log.info("Getting user id: {}", id);
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден");
        }
        UserDto userDto = userRowMapper.toUserDto(user);
        log.info("Found user: {}", userDto);
        return userDto;
    }

    public void deleteUser(Long id) {
        log.info("Deleting user id: {}", id);
        getUser(id);
        users.remove(id);
        log.info("Deleted user id: {}", id);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Updating user id: {}, user: {}", id, userDto.toString());
        getUser(id);
        User user = users.get(id);
        user.setId(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateUserEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        users.put(id, user);
        UserDto userDto1 = userRowMapper.toUserDto(users.get(id));
        log.info("Updated user: {}", userDto1);
        return userDto1;
    }

    public void validateUserEmail(String email) {
        for (User userOfMap : users.values()) {
            if (email.equals(userOfMap.getEmail())) {
                throw new ConflictException("Пользователь с таким email: " + email + " уже существует ");
            }
        }
    }
}
