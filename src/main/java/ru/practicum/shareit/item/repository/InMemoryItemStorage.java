package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemRowMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemStorage {
    private final ItemRowMapper itemRowMapper = new ItemRowMapper();

    private final Map<Long, Item> items = new HashMap<>();
    private Long countId = 0L;

    public ItemDto addItem(Long id, ItemDto itemDto) {
        Item item = Item.builder()
                .id(getCountId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(id)
                .request(itemDto.getId())
                .build();
        items.put(item.getId(), item);
        return itemRowMapper.toItemDto(items.get(item.getId()));
    }

    public ItemDto getItem(Long id) {
        ItemDto itemDto = itemRowMapper.toItemDto(items.get(id));
        if (itemDto == null) {
            throw new NotFoundException("Вещь с id: " + id + " не найдена");
        }
        return itemDto;
    }

    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        Item item = items.get(id);
        if (item.getOwner().equals(userId)) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
        }
        items.put(id, item);
        return itemRowMapper.toItemDto(items.get(id));
    }

    public List<ItemDto> getAllItemsOfUser(Long userId) {
        List<Item> itemList = new ArrayList<>(items.values());
        itemList.removeIf(item -> !item.getOwner().equals(userId));
        List<ItemDto> itemDtoList = new ArrayList<>(itemList.size());
        itemList.forEach(item -> itemDtoList.add(itemRowMapper.toItemDto(item)));
        return itemDtoList;
    }

    public List<ItemDto> searchItemsForRental(String text) {
        List<Item> itemList = new ArrayList<>(items.values());
        itemList.removeIf(item -> !item.getAvailable());
        itemList.removeIf(item -> !item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(text.toLowerCase()));
        List<ItemDto> itemDtoList = new ArrayList<>(itemList.size());
        itemList.forEach(item -> itemDtoList.add(itemRowMapper.toItemDto(item)));
        return itemDtoList;
    }

    public Long getCountId() {
        return ++countId;
    }
}
