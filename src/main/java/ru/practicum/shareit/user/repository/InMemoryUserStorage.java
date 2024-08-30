package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mappers.UserRowMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserStorage {
    private final UserRowMapper userRowMapper = new UserRowMapper();

    private final Map<Long, User> users = new HashMap<Long, User>();
    private Long countId = 0L;

    public User addUser(User user) {
        validateUser(user);
        user.setId(getCountId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    public User getUser(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден");
        }
        return users.get(id);
    }

    public void deleteUser(Long id) {
        getUser(id);
        users.remove(id);
    }

    public User updateUser(Long id, User user) {
        getUser(id);
        user.setId(id);
        if (user.getName() == null) {
            user.setName(users.get(id).getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(users.get(id).getEmail());
        } else {
            validateUser(user);
        }
        users.put(id, user);
        return users.get(id);
    }

    public Long getCountId() {
        return ++countId;
    }

    public void validateUser(User user) {
        for (User userOfMap : users.values()) {
            if (user.getEmail().equals(userOfMap.getEmail())) {
                throw new ConflictException("Пользователь с таким email: " + user.getEmail() + " уже существует ");
            }
        }
    }
}
