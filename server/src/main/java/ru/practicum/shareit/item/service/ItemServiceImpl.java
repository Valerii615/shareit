package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingDbStorage;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentText;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoTime;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentDbStorage;
import ru.practicum.shareit.item.repository.ItemDbStorage;
import ru.practicum.shareit.request.repository.RequestDbStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final BookingDbStorage bookingDbStorage;
    private final ItemDbStorage itemDbStorage;
    private final UserServiceImpl userService;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentDbStorage commentDbStorage;
    private final CommentMapper commentMapper;
    private final RequestDbStorage requestDbStorage;

    @Override
    @Transactional
    public Item createItem(Long id, ItemDto itemDto) {
        log.info("Creating new item: {}", itemDto);
        User user = userService.findUserById(id);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            item.setRequest(requestDbStorage.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request with id:" + itemDto.getRequestId() + " not found")));
        }
        Item createdItem = itemDbStorage.save(item);
        log.info("Item created: {}", createdItem);
        return createdItem;
    }

    @Override
    public ItemDtoTime findItemDtoTimeById(Long id) {
        log.info("Finding itemDtoTime by id: {}", id);
        ItemDtoTime itemDtoTime = itemMapper.toItemDtoTime(findItemById(id));
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTimeNow = LocalDateTime.ofInstant(Instant.now(), zoneId);
        Booking[] bookings = new Booking[2];
        bookings[0] = bookingDbStorage.findByItemIdAndStartBeforeOrderByStart(id, localDateTimeNow.minusSeconds(30));
        bookings[1] = bookingDbStorage.findByItemIdAndStartAfterOrderByStart(id, localDateTimeNow);
        if (bookings[0] != null) itemDtoTime.setLastBooking(bookingMapper.toBookingDto(bookings[0]));
        if (bookings[1] != null) itemDtoTime.setNextBooking(bookingMapper.toBookingDto(bookings[1]));
        itemDtoTime.setComments(commentDbStorage.findByItemId(id).stream().map(commentMapper::toDto).toList());
        log.info("ItemDtoTime find: {}", itemDtoTime);
        return itemDtoTime;
    }

    @Override
    public Item findItemById(Long id) {
        log.info("Finding item by id: {}", id);
        Item item = itemDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id: " + id + " not found"));
        User owner = item.getOwner();
        item.setOwner(owner);
        log.info("Item find: {}", item);
        return item;
    }

    public List<Item> findItemByRequestId(Long requestId) {
        log.info("Finding item by request id: {}", requestId);
        List<Item> itemList = itemDbStorage.findByRequestId(requestId);
        log.info("Found {} items", itemList.size());
        return itemList;
    }

    @Override
    @Transactional
    public Item updateItem(Long userId, Long id, ItemDto itemDto) {
        log.info("Updating item: {}", itemDto);
        userService.findUserById(userId);
        Item item = findItemById(id);
        Item newItem = itemMapper.toItem(itemDto);
        newItem.setId(id);
        newItem.setOwner(item.getOwner());
        if (item.getOwner().getId().equals(userId)) {
            if (newItem.getName() == null) {
                newItem.setName(item.getName());
            }
            if (newItem.getDescription() == null) {
                newItem.setDescription(item.getDescription());
            }
            if (newItem.getAvailable() == null) {
                newItem.setAvailable(item.getAvailable());
            }
        } else {
            throw new ConflictException("Only the owner of the item can edit it");
        }
        Item itemUpdated = itemDbStorage.save(newItem);
        log.info("ItemDto update: {}", itemUpdated);
        return itemUpdated;
    }

    @Override
    public List<ItemDto> getAllItemsOfUser(Long userId) {
        log.info("Getting all items of user: {}", userId);
        userService.findUserById(userId);
        List<ItemDto> itemDtoList = itemDbStorage.findItemByOwnerId(userId).stream()
                .map(itemMapper::toItemDto)
                .toList();
        log.info("ItemDtoListUser: {}", itemDtoList);
        return itemDtoList;
    }

    @Override
    public List<ItemDto> searchItemsForRental(String text) {
        log.info("Searching for items for rental: {}", text);
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        } else {
            List<ItemDto> itemDtoList = itemDbStorage
                    .findItemByAvailableIsTrueAndNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase(text, text)
                    .stream()
                    .map(itemMapper::toItemDto)
                    .toList();
            log.info("ItemDtoListRental: {}", itemDtoList);
            return itemDtoList;
        }
    }

    @Override
    @Transactional
    public CommentDto addComment(Long id, Long itemId, CommentText commentText) {
        log.info("Adding comment: {}", commentText);
        User user = userService.findUserById(id);
        Item item = findItemById(itemId);
        List<Booking> bookings = bookingDbStorage.findByItemIdAndBookerIdAndEndBefore(itemId, id, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadRequest("The user with id:" + id + " did not rent an item with id:" + itemId);
        }
        Comment comment = Comment.builder()
                .text(commentText.getText())
                .item(item)
                .author(user)
                .createdDate(LocalDateTime.now())
                .build();
        CommentDto commentDto = commentMapper.toDto(commentDbStorage.save(comment));
        log.info("CommentDto: {}", commentDto);
        return commentDto;
    }
}
