package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDbStorage;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService implements ItemServiceInterface {
    private final ItemDbStorage itemDbStorage;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    public ItemDto createItem(Long id, ItemDto itemDto) {
        User user = userMapper.toUser(userService.findUserById(id));
        user.setId(id);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        return itemMapper.toItemDto(itemDbStorage.save(item));
    }

    @Override
    public ItemDto findItemById(Long id) {
        return itemMapper.toItemDto(itemDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + id + " не найдена")));
    }

    @Override
    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        userService.findUserById(userId);
        Item item = itemDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + id + " не найдена"));
        User owner = item.getOwner();
        Item newItem = itemMapper.toItem(itemDto);
        newItem.setId(id);
        newItem.setOwner(owner);
        if (owner.getId().equals(userId)) {
            if (newItem.getName() == null) {
                newItem.setName(item.getName());
            }
            if (newItem.getDescription() == null) {
                newItem.setDescription(item.getDescription());
            }
            if (newItem.getAvailable() == null) {
                newItem.setAvailable(item.getAvailable());
            }
        }
        return itemMapper.toItemDto(itemDbStorage.save(newItem));
    }

    @Override
    public List<ItemDto> getAllItemsOfUser(Long userId) {
        userService.findUserById(userId);
        return itemDbStorage.findItemByOwnerId(userId).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItemsForRental(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return itemDbStorage.findItemByAvailableIsTrueAndNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase(text, text).stream()
                    .map(itemMapper::toItemDto)
                    .toList();
        }
    }
}
