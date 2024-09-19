package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserDbStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserDbStorage userDbStorage;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user: {}", userDto);
        User user = userMapper.toUser(userDto);
        UserDto userDto1 = userMapper.toUserDto(userDbStorage.save(user));
        log.info("User created: {}", userDto1);
        return userDto1;
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Updating user: {}", userDto);
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
        UserDto userDto1 = userMapper.toUserDto(userDbStorage.save(newUser));
        log.info("User updated: {}", userDto1);
        return userDto1;
    }

    @Override
    public UserDto findUserById(Long id) {
        log.info("Finding user by id: {}", id);
        UserDto userDto = userMapper.toUserDto(userDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден")));
        log.info("User found: {}", userDto);
        return userDto;
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        log.info("Deleting user by id: {}", id);
        userDbStorage.deleteById(id);
        log.info("User deleted: {}", id);
    }
}
