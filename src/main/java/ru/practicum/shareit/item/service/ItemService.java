package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long id, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long id, ItemDto itemDto);

    ItemDto findItemDtoById(Long id);

    Item findItemById(Long id);

    List<ItemDto> getAllItemsOfUser(Long userId);

    List<ItemDto> searchItemsForRental(String text);
}
