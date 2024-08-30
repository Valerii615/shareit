package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserRowMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserStorage {
    private final UserRowMapper userRowMapper = new UserRowMapper();

    private final Map<Long, User> users = new HashMap<Long, User>();
    private Long countId = 0L;

    public UserDto addUser(UserDto userDto) {
        validateUserEmail(userDto.getEmail());
        User user = User.builder()
                .id(getCountId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
        users.put(user.getId(), user);
        return userRowMapper.toUserDto(users.get(user.getId()));
    }

    public UserDto getUser(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден");
        }
        return userRowMapper.toUserDto(user);
    }

    public void deleteUser(Long id) {
        getUser(id);
        users.remove(id);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        getUser(id);
        User user = new User();
        user.setId(id);
        if (userDto.getName() == null) {
            user.setName(users.get(id).getName());
        } else {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() == null) {
            user.setEmail(users.get(id).getEmail());
        } else {
            validateUserEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        users.put(id, user);
        return userRowMapper.toUserDto(users.get(id));
    }

    public Long getCountId() {
        return ++countId;
    }

    public void validateUserEmail(String email) {
        for (User userOfMap : users.values()) {
            if (email.equals(userOfMap.getEmail())) {
                throw new ConflictException("Пользователь с таким email: " + email + " уже существует ");
            }
        }
    }
}
