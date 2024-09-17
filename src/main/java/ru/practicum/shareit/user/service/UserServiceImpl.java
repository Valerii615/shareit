package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserDbStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDbStorage userDbStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userDbStorage.save(user));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User newUser = userMapper.toUser(userDto);
        User user = userDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
        newUser.setId(id);
        if (newUser.getName() == null) {
            newUser.setName(user.getName());
        }
        if (newUser.getEmail() == null) {
            newUser.setEmail(user.getEmail());
        }
        return userMapper.toUserDto(userDbStorage.save(newUser));
    }

    @Override
    public UserDto findUserById(Long id) {
        return userMapper.toUserDto(userDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден")));
    }

    @Override
    public void deleteUserById(Long id) {
        userDbStorage.deleteById(id);
    }
}
