package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor
public class UserServiceImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Test
    @DirtiesContext
    public void createUser() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        User user2 = userServiceImpl.createUser(new UserDto(null, "user2", "www2@mail.com"));
        User user3 = userServiceImpl.createUser(new UserDto(null, "user3", "www3@mail.com"));

        try {
            userServiceImpl.createUser(new UserDto(1L, "user1", "www1@mail.com"));
        } catch (ConflictException e) {
            assertEquals("could not execute statement [Нарушение уникального индекса или первичного" +
                            " ключа: \"PUBLIC.UQ_USER_EMAIL_INDEX_4 ON PUBLIC.USERS(EMAIL NULLS FIRST) VALUES" +
                            " ( /* 1 */ 'www1@mail.com' )\"\n" +
                            "Unique index or primary key violation: \"PUBLIC.UQ_USER_EMAIL_INDEX_4 ON PUBLIC.USERS" +
                            "(EMAIL NULLS FIRST) VALUES ( /* 1 */ 'www1@mail.com' )\"; SQL statement:\n" +
                            "insert into users (email,name,user_id) values (?,?,default) [23505-224]] [insert into " +
                            "users (email,name,user_id) values (?,?,default)]; SQL [insert into users" +
                            " (email,name,user_id) values (?,?,default)]; constraint [PUBLIC.UQ_USER_EMAIL_INDEX_4]",
                    e.getMessage(), "Получено неверное исключение");
        }

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User userTest1 = query.setParameter("email", user1.getEmail()).getSingleResult();
        User userTest2 = query.setParameter("email", user2.getEmail()).getSingleResult();
        User userTest3 = query.setParameter("email", user3.getEmail()).getSingleResult();

        assertThat(userTest1.getId(), notNullValue());
        assertThat(userTest1.getName(), equalTo(user1.getName()));
        assertThat(userTest1.getEmail(), equalTo(user1.getEmail()));

        assertThat(userTest2.getId(), notNullValue());
        assertThat(userTest2.getName(), equalTo(user2.getName()));
        assertThat(userTest2.getEmail(), equalTo(user2.getEmail()));

        assertThat(userTest3.getId(), notNullValue());
        assertThat(userTest3.getName(), equalTo(user3.getName()));
        assertThat(userTest3.getEmail(), equalTo(user3.getEmail()));

    }

    @Test
    @DirtiesContext
    public void updateUser() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        User user2 = userServiceImpl.createUser(new UserDto(null, "user2", "www2@mail.com"));
        User user3 = userServiceImpl.createUser(new UserDto(null, "user3", "www3@mail.com"));

        User user1Up = userServiceImpl.updateUser(1L, new UserDto(null, "user1Up", "www1@mail.com"));
        User user2Up = userServiceImpl.updateUser(2L, new UserDto(null, "user2Up", "www2Up@mail.com"));
        User user3Up = userServiceImpl.updateUser(3L, new UserDto(null, "user3", "www3@mail.com"));

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User userTest1 = query.setParameter("email", user1Up.getEmail()).getSingleResult();
        User userTest2 = query.setParameter("email", user2Up.getEmail()).getSingleResult();
        User userTest3 = query.setParameter("email", user3Up.getEmail()).getSingleResult();

        assertThat(userTest1.getId(), equalTo(user1.getId()));
        assertThat(userTest1.getName(), equalTo(user1Up.getName()));
        assertThat(userTest1.getEmail(), equalTo(user1Up.getEmail()));

        assertThat(userTest2.getId(), equalTo(user2.getId()));
        assertThat(userTest2.getName(), equalTo(user2Up.getName()));
        assertThat(userTest2.getEmail(), equalTo(user2Up.getEmail()));

        assertThat(userTest3.getId(), equalTo(user3.getId()));
        assertThat(userTest3.getName(), equalTo(user3Up.getName()));
        assertThat(userTest3.getEmail(), equalTo(user3Up.getEmail()));
    }

    @Test
    @DirtiesContext
    public void findUserById() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        User user2 = userServiceImpl.createUser(new UserDto(null, "user2", "www2@mail.com"));
        User user3 = userServiceImpl.createUser(new UserDto(null, "user3", "www3@mail.com"));

        User user1Fd = userServiceImpl.findUserById(user1.getId());
        User user2Fd = userServiceImpl.findUserById(user2.getId());
        User user3Fd = userServiceImpl.findUserById(user3.getId());
        try {
            userServiceImpl.deleteUserById(999L);
        } catch (Exception e) {
            assertEquals("User with id: 999 not found", e.getMessage(), "Получено неверное исключение");
        }

        assertThat(user1Fd.getId(), equalTo(user1.getId()));
        assertThat(user1Fd.getName(), equalTo(user1.getName()));
        assertThat(user1Fd.getEmail(), equalTo(user1.getEmail()));

        assertThat(user2Fd.getId(), equalTo(user2.getId()));
        assertThat(user2Fd.getName(), equalTo(user2.getName()));
        assertThat(user2Fd.getEmail(), equalTo(user2.getEmail()));

        assertThat(user3Fd.getId(), equalTo(user3.getId()));
        assertThat(user3Fd.getName(), equalTo(user3.getName()));
        assertThat(user3Fd.getEmail(), equalTo(user3.getEmail()));
    }

    @Test
    @DirtiesContext
    public void deleteUserById() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        User user2 = userServiceImpl.createUser(new UserDto(null, "user2", "www2@mail.com"));
        User user3 = userServiceImpl.createUser(new UserDto(null, "user3", "www3@mail.com"));

        userServiceImpl.deleteUserById(user1.getId());
        userServiceImpl.deleteUserById(user2.getId());
        userServiceImpl.deleteUserById(user3.getId());

        try {
            userServiceImpl.deleteUserById(999L);
        } catch (Exception e) {
            assertEquals("User with id: 999 not found", e.getMessage(), "Получено неверное исключение");
        }

        try {
            userServiceImpl.findUserById(user1.getId());
        } catch (Exception e) {
            assertEquals("User with id: 1 not found", e.getMessage(), "Получено неверное исключение");
        }

        try {
            userServiceImpl.findUserById(user2.getId());
        } catch (Exception e) {
            assertEquals("User with id: 2 not found", e.getMessage(), "Получено неверное исключение");
        }

        try {
            userServiceImpl.findUserById(user2.getId());
        } catch (Exception e) {
            assertEquals("User with id: 2 not found", e.getMessage(), "Получено неверное исключение");
        }
    }
}
