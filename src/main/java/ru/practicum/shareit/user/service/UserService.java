package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserStorage;

@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addUser(User user) {
        return inMemoryUserStorage.addUser(user);
    }

    public User getUser(Long id) {
        return inMemoryUserStorage.getUser(id);
    }

    public void deleteUser(Long id) {
        inMemoryUserStorage.deleteUser(id);
    }

    public User updateUser(Long id, User user) {
        return inMemoryUserStorage.updateUser(id, user);
    }
}
