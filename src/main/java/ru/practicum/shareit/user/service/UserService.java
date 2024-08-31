package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.InMemoryUserStorage;

@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public UserDto addUser(UserDto userDto) {
        return inMemoryUserStorage.addUser(userDto);
    }

    public UserDto getUser(Long id) {
        return inMemoryUserStorage.getUser(id);
    }

    public void deleteUser(Long id) {
        inMemoryUserStorage.deleteUser(id);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        return inMemoryUserStorage.updateUser(id, userDto);
    }
}
