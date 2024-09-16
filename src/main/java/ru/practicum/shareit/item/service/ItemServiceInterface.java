package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemServiceInterface {
    ItemDto createItem(Long id, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long id, ItemDto itemDto);

    ItemDto findItemById(Long id);

    List<ItemDto> getAllItemsOfUser(Long userId);

    List<ItemDto> searchItemsForRental(String text);
}
