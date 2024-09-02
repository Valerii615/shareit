package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserStorage;

@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserMapper userMapper;

    public UserService(InMemoryUserStorage inMemoryUserStorage, UserMapper userMapper) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userMapper = userMapper;
    }

    public UserDto addUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(inMemoryUserStorage.addUser(user));
    }

    public UserDto getUser(Long id) {
        return userMapper.toUserDto(inMemoryUserStorage.getUser(id));
    }

    public void deleteUser(Long id) {
        inMemoryUserStorage.deleteUser(id);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(inMemoryUserStorage.updateUser(id, user));
    }
}
