package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
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
    public User createUser(UserDto userDto) {
        log.info("Creating user: {}", userDto);
        User user;
        try {
            user = userDbStorage.save(userMapper.toUser(userDto));
        } catch (Exception e) {
            log.error("Duplicate key value (email) violates the uniqueness constraint (Creating)");
            throw new ConflictException(e.getMessage());
        }
        log.info("User created: {}", user);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserDto userDto) {
        log.info("Updating user: {}", userDto);
        User user = findUserById(id);
        User newUser = userMapper.toUser(userDto);
        newUser.setId(id);
        if (newUser.getName() == null) newUser.setName(user.getName());
        if (newUser.getEmail() == null) newUser.setEmail(user.getEmail());
        User updatedUser;
        try {
            updatedUser = userDbStorage.save(newUser);
        } catch (Exception e) {
            log.error("Duplicate key value (email) violates the uniqueness constraint (Updating)");
            throw new ConflictException(e.getMessage());
        }
        log.info("User updated: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public User findUserById(Long id) {
        log.info("Finding user by id: {}", id);
        User user = userDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
        log.info("User found: {}", user);
        return user;
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        log.info("Deleting user by id: {}", id);
        findUserById(id);
        userDbStorage.deleteById(id);
        log.info("User deleted: {}", id);
    }
}
