package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDbStorage extends JpaRepository<Item, Long> {
    List<Item> findItemByOwnerId(Long ownerId);

    List<Item> findItemByAvailableIsTrueAndNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase(String nameText, String descriptionText);
}
