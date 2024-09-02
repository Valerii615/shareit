package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryUserStorage {
    private final UserMapper userMapper = new UserMapper();

    private final Map<Long, User> users = new HashMap<>();
    private Long countId = 0L;

    public User addUser(User user) {
        log.info("Adding new user: {}", user);
        validateUserEmail(user.getEmail());
        user.setId(++countId);
        users.put(user.getId(), user);
        log.info("Added user: {}", user);
        return user;
    }

    public User getUser(Long id) {
        log.info("Getting user id: {}", id);
        User user = users.get(id);
        if (user == null) {
            log.error("NotFoundException: Пользователь с id: {} не найден", id);
            throw new NotFoundException("Пользователь с id: " + id + " не найден");
        }
        log.info("Found user: {}", user);
        return user;
    }

    public void deleteUser(Long id) {
        log.info("Deleting user id: {}", id);
        getUser(id);
        users.remove(id);
        log.info("Deleted user id: {}", id);
    }

    public User updateUser(Long id, User newUser) {
        log.info("Updating user id: {}, user: {}", id, newUser.toString());
        User user = getUser(id);
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            validateUserEmail(newUser.getEmail());
            user.setEmail(newUser.getEmail());
        }
        users.put(id, user);
        log.info("Updated user: {}", user);
        return user;
    }

    public void validateUserEmail(String email) {
        for (User userOfMap : users.values()) {
            if (email.equals(userOfMap.getEmail())) {
                log.error("ConflictException: Пользователь с таким email: {} уже существует ", email);
                throw new ConflictException("Пользователь с таким email: " + email + " уже существует ");
            }
        }
    }
}
