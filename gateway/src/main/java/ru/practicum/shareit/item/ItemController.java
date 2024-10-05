package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentText;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long id, @RequestBody ItemDto itemDto) {
        return itemClient.addItem(id, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @PathVariable Long itemId,
                                             @RequestBody CommentText commentText) {
        return itemClient.addComment(id, itemId, commentText);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Long id) {
        return itemClient.getItem(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long id,
                              @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, id, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getAllItemsOfUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsForRental(@RequestHeader("X-Sharer-User-Id") Long id,
                                                       @RequestParam(value = "text") String text) {
        return itemClient.searchItemsForRental(id, text);
    }
}
