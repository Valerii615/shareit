package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long countId = 0L;

    public Item addItem(Long id, Item item) {
        log.info("Adding item {}", item);
        item.setId(++countId);
//        item.setOwner(id);
        items.put(item.getId(), item);
        log.info("Added item {}", item);
        return item;
    }

    public Item getItem(Long id) {
        log.info("Getting item {}", id);
        Item item = items.get(id);
        if (item == null) {
            log.error("NotFoundException: Вещь с id: {} не найдена", id);
            throw new NotFoundException("Вещь с id: " + id + " не найдена");
        }
        log.info("Found item {}", item);
        return item;
    }

    public Item updateItem(Long userId, Long id, Item newItem) {
        log.info("Updating userId: {}, item: {}", userId, newItem);
        Item item = items.get(id);
        if (item.getOwner().equals(userId)) {
            if (newItem.getName() != null) {
                item.setName(newItem.getName());
            }
            if (newItem.getDescription() != null) {
                item.setDescription(newItem.getDescription());
            }
            if (newItem.getAvailable() != null) {
                item.setAvailable(newItem.getAvailable());
            }
        }
        items.put(id, item);
        log.info("Updated item {}", item);
        return item;
    }

    public List<Item> getAllItemsOfUser(Long userId) {
        log.info("Getting all items of user {}", userId);
        List<Item> itemList = new ArrayList<>(items.values()).stream()
                .filter(item -> item.getOwner().equals(userId))
                .toList();
        log.info("Found {} items of user", itemList.size());
        return itemList;
    }

    public List<Item> searchItemsForRental(String text) {
        log.info("Searching for items for {}", text);
        if (text == null || text.isEmpty()) {
            log.info("Found 0 items for rental");
            return new ArrayList<>();
        } else {
            List<Item> itemList = new ArrayList<>(items.values()).stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .toList();
            log.info("Found {} items for rental", itemList.size());
            return itemList;
        }
    }
}
