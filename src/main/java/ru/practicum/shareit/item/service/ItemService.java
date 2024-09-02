package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
public class ItemService {
    private final InMemoryItemStorage inMemoryItemStorage;
    private final UserService userService;
    private final ItemMapper itemMapper;

    public ItemService(InMemoryItemStorage inMemoryItemStorage, UserService userService, ItemMapper itemMapper) {
        this.inMemoryItemStorage = inMemoryItemStorage;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    public ItemDto addItem(Long id, ItemDto itemDto) {
        userService.getUser(id);
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(inMemoryItemStorage.addItem(id, item));
    }

    public ItemDto getItem(Long id) {
        return itemMapper.toItemDto(inMemoryItemStorage.getItem(id));
    }

    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        userService.getUser(userId);
        getItem(id);
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(inMemoryItemStorage.updateItem(userId, id, item));
    }

    public List<ItemDto> getAllItemsOfUser(Long userId) {
        userService.getUser(userId);
        return inMemoryItemStorage.getAllItemsOfUser(userId).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    public List<ItemDto> searchItemsForRental(String text) {
        return inMemoryItemStorage.searchItemsForRental(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }
}
