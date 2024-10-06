package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoTime;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor
public class ItemServiceImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Test
    @DirtiesContext
    public void createItem() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        User user2 = userServiceImpl.createUser(new UserDto(null, "user2", "www2@mail.com"));
        User user3 = userServiceImpl.createUser(new UserDto(null, "user3", "www3@mail.com"));
        Item item1 = itemService.createItem(user1.getId(), new ItemDto(null, "item1", "des1", true, null));
        Item item2 = itemService.createItem(user2.getId(), new ItemDto(null, "item2", "des2", false, null));
        Item item3 = itemService.createItem(user3.getId(), new ItemDto(null, "item3", "des3", true, null));

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        Item item1Db = query.setParameter("id", item1.getId()).getSingleResult();
        Item item2Db = query.setParameter("id", item2.getId()).getSingleResult();
        Item item3Db = query.setParameter("id", item3.getId()).getSingleResult();

        assertThat(item1Db.getId(), notNullValue());
        assertThat(item2Db.getId(), notNullValue());
        assertThat(item3Db.getId(), notNullValue());

        assertThat(item1Db.getName(), equalTo(item1.getName()));
        assertThat(item2Db.getName(), equalTo(item2.getName()));
        assertThat(item3Db.getName(), equalTo(item3.getName()));

        assertThat(item1Db.getDescription(), equalTo(item1.getDescription()));
        assertThat(item2Db.getDescription(), equalTo(item2.getDescription()));
        assertThat(item3Db.getDescription(), equalTo(item3.getDescription()));
    }

    @Test
    @DirtiesContext
    public void updateItem() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        User user2 = userServiceImpl.createUser(new UserDto(null, "user2", "www2@mail.com"));
        User user3 = userServiceImpl.createUser(new UserDto(null, "user3", "www3@mail.com"));
        Item item1 = itemService.createItem(user1.getId(), new ItemDto(null, "item1", "des1", true, null));
        Item item2 = itemService.createItem(user2.getId(), new ItemDto(null, "item2", "des2", false, null));
        Item item3 = itemService.createItem(user3.getId(), new ItemDto(null, "item3", "des3", true, null));

        Item item1Up = itemService.updateItem(user1.getId(), item1.getId(), new ItemDto(null, "item1up", "des1up", true, null));
        Item item2Up = itemService.updateItem(user2.getId(), item2.getId(), new ItemDto(null, "item2up", "des2up", true, null));
        Item item3Up = itemService.updateItem(user3.getId(), item3.getId(), new ItemDto(null, "item3up", "des3up", true, null));

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        Item item1Db = query.setParameter("id", item1Up.getId()).getSingleResult();
        Item item2Db = query.setParameter("id", item2Up.getId()).getSingleResult();
        Item item3Db = query.setParameter("id", item3Up.getId()).getSingleResult();

        assertThat(item1Db.getId(), equalTo(item1Up.getId()));
        assertThat(item2Db.getId(), equalTo(item2Up.getId()));
        assertThat(item3Db.getId(), equalTo(item3Up.getId()));

        assertThat(item1Db.getName(), equalTo(item1Up.getName()));
        assertThat(item2Db.getName(), equalTo(item2Up.getName()));
        assertThat(item3Db.getName(), equalTo(item3Up.getName()));

        assertThat(item1Db.getDescription(), equalTo(item1Up.getDescription()));
        assertThat(item2Db.getDescription(), equalTo(item2Up.getDescription()));
        assertThat(item3Db.getDescription(), equalTo(item3Up.getDescription()));
    }

    @Test
    @DirtiesContext
    public void getItem() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        User user2 = userServiceImpl.createUser(new UserDto(null, "user2", "www2@mail.com"));
        User user3 = userServiceImpl.createUser(new UserDto(null, "user3", "www3@mail.com"));
        Item item1 = itemService.createItem(user1.getId(), new ItemDto(null, "item1", "des1", true, null));
        Item item2 = itemService.createItem(user2.getId(), new ItemDto(null, "item2", "des2", false, null));
        Item item3 = itemService.createItem(user3.getId(), new ItemDto(null, "item3", "des3", true, null));

        ItemDtoTime itemDtoTime1 = itemService.findItemDtoTimeById(item1.getId());
        ItemDtoTime itemDtoTime2 = itemService.findItemDtoTimeById(item2.getId());
        ItemDtoTime itemDtoTime3 = itemService.findItemDtoTimeById(item3.getId());

        assertThat(itemDtoTime1.getId(), equalTo(item1.getId()));
        assertThat(itemDtoTime2.getId(), equalTo(item2.getId()));
        assertThat(itemDtoTime3.getId(), equalTo(item3.getId()));

        assertThat(itemDtoTime1.getName(), equalTo(item1.getName()));
        assertThat(itemDtoTime2.getName(), equalTo(item2.getName()));
        assertThat(itemDtoTime3.getName(), equalTo(item3.getName()));

        assertThat(itemDtoTime1.getDescription(), equalTo(item1.getDescription()));
        assertThat(itemDtoTime2.getDescription(), equalTo(item2.getDescription()));
        assertThat(itemDtoTime3.getDescription(), equalTo(item3.getDescription()));
    }

    @Test
    @DirtiesContext
    public void getAllItemsOfUser() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        itemService.createItem(user1.getId(), new ItemDto(null, "item1", "des1", true, null));
        itemService.createItem(user1.getId(), new ItemDto(null, "item2", "des2", false, null));
        itemService.createItem(user1.getId(), new ItemDto(null, "item3", "des3", true, null));

        List<ItemDto> itemList =  itemService.getAllItemsOfUser(user1.getId());
        assertEquals(3, itemList.size(), "Получена неверная длинна списка вещей");
    }

    @Test
    @DirtiesContext
    public void searchItemsForRental() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        itemService.createItem(user1.getId(), new ItemDto(null, "item1", "des1", true, null));
        itemService.createItem(user1.getId(), new ItemDto(null, "item2", "des2", false, null));
        itemService.createItem(user1.getId(), new ItemDto(null, "item3", "des3", true, null));

        List<ItemDto> itemDtoList = itemService.searchItemsForRental("item");

        assertEquals(2, itemDtoList.size(), "Получена неверная длинна списка вещей");
    }
}
