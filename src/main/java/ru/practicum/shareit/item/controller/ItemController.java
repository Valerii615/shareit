package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentText;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoTime;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long id, @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(id, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long id,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentText commentText) {
        return itemService.addComment(id, itemId, commentText);
    }

    @GetMapping("/{id}")
    public ItemDtoTime getItem(@PathVariable Long id) {
        return itemService.findItemDtoTimeById(id);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long id,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, id, itemDto);
    }

    @GetMapping
    public List<ItemDto> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsOfUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsForRental(@RequestParam(value = "text") String text) {
        return itemService.searchItemsForRental(text);
    }
}
