package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.InMemoryItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
public class ItemService {
    private final InMemoryItemStorage inMemoryItemStorage;
    private final UserService userService;

    public ItemService(InMemoryItemStorage inMemoryItemStorage, UserService userService) {
        this.inMemoryItemStorage = inMemoryItemStorage;
        this.userService = userService;
    }

    public ItemDto addItem(Long id, ItemDto itemDto) {
        userService.getUser(id);
        return inMemoryItemStorage.addItem(id, itemDto);
    }

    public ItemDto getItem(Long id) {
        return inMemoryItemStorage.getItem(id);
    }

    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        userService.getUser(userId);
        getItem(id);
        return inMemoryItemStorage.updateItem(userId, id, itemDto);
    }

    public List<ItemDto> getAllItemsOfUser(Long userId) {
        userService.getUser(userId);
        return inMemoryItemStorage.getAllItemsOfUser(userId);
    }

    public List<ItemDto> searchItemsForRental(String text) {
        return inMemoryItemStorage.searchItemsForRental(text);
    }
}
