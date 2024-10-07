package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentText;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoTime;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Long id, ItemDto itemDto);

    Item updateItem(Long userId, Long id, ItemDto itemDto);

    Item findItemById(Long id);

    List<ItemDto> getAllItemsOfUser(Long userId);

    List<ItemDto> searchItemsForRental(String text);

    ItemDtoTime findItemDtoTimeById(Long id);

    CommentDto addComment(Long id, Long itemId, CommentText commentText);
}
