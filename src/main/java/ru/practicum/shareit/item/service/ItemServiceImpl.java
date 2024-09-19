package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingDbStorage;
import ru.practicum.shareit.exception.BadRequest;
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
import ru.practicum.shareit.user.mappers.UserMapper;
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
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;
    private final CommentDbStorage commentDbStorage;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto createItem(Long id, ItemDto itemDto) {
        log.info("Creating new item: {}", itemDto);
        User user = userMapper.toUser(userService.findUserById(id));
        user.setId(id);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        ItemDto itemDto1 = itemMapper.toItemDto(itemDbStorage.save(item));
        log.info("Item created: {}", itemDto1);
        return itemDto1;
    }

    @Override
    public ItemDto findItemDtoById(Long id) {
        log.info("Finding itemDto by id: {}", id);
        ItemDto itemDto = itemMapper.toItemDto(itemDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + id + " не найдена")));
        log.info("ItemDto find: {}", itemDto);
        return itemDto;
    }

    @Override
    public ItemDtoTime findItemDtoTimeById(Long id) {
        log.info("Finding itemDtoTime by id: {}", id);
        ItemDtoTime itemDtoTime = itemMapper.toItemDtoTime(itemDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + id + " не найдена")));
        Booking[] bookings = getBeforeAndAfterBooking(id);
        if (bookings[0] != null) {
            itemDtoTime.setLastBooking(bookingMapper.toBookingDto(bookings[0]));
        } else {
            itemDtoTime.setLastBooking(null);
        }
        if (bookings[1] != null) {
            itemDtoTime.setNextBooking(bookingMapper.toBookingDto(bookings[1]));
        } else {
            itemDtoTime.setNextBooking(null);
        }
        itemDtoTime.setComments(commentDbStorage.findByItemId(id).stream().map(commentMapper::toDto).toList());
        log.info("ItemDtoTime find: {}", itemDtoTime);
        return itemDtoTime;
    }

    public Booking[] getBeforeAndAfterBooking(Long itemId) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTimeNow = LocalDateTime.ofInstant(Instant.now(), zoneId);
        Booking[] bookings = new Booking[2];
        bookings[0] = bookingDbStorage.findByItemIdAndStartBeforeOrderByStart(itemId, localDateTimeNow.minusSeconds(30));
        bookings[1] = bookingDbStorage.findByItemIdAndStartAfterOrderByStart(itemId, localDateTimeNow);
        return bookings;
    }

    @Override
    public Item findItemById(Long id) {
        log.info("Finding item by id: {}", id);
        Item item = itemDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + id + " не найдена"));
        item.setId(id);
        User owner = item.getOwner();
        item.setOwner(owner);
        log.info("Item find: {}", item);
        return item;
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        log.info("Updating item: {}", itemDto);
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
        ItemDto itemDto1 = itemMapper.toItemDto(itemDbStorage.save(newItem));
        log.info("ItemDto update: {}", itemDto1);
        return itemDto1;
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
        User user = userMapper.toUser(userService.findUserById(id));
        Item item = findItemById(itemId);
        List<Booking> booking = bookingDbStorage.findByItemIdAndBookerIdAndEndBefore(itemId, id, LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new BadRequest("Пользователь с id:" + id + " не арендовал вещь с id:" + itemId);
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
