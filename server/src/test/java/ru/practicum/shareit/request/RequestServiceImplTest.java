package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItem;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor
public class RequestServiceImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserService userServiceImpl;

    @Autowired
    private UserMapper userMapper;

    @Test
    @DirtiesContext
    public void createRequest() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        Request request1 = requestService.createRequest(user1.getId(), new RequestDto(null, "request1", userMapper.toUserDto(user1), null));
        Request request2 = requestService.createRequest(user1.getId(), new RequestDto(null, "request2", userMapper.toUserDto(user1), null));
        Request request3 = requestService.createRequest(user1.getId(), new RequestDto(null, "request3", userMapper.toUserDto(user1), null));

        TypedQuery<Request> query = em.createQuery("SELECT r FROM Request r where r.id = :id", Request.class);
        Request requestDb1 = query.setParameter("id", request1.getId()).getSingleResult();
        Request requestDb2 = query.setParameter("id", request2.getId()).getSingleResult();
        Request requestDb3 = query.setParameter("id", request3.getId()).getSingleResult();

        assertThat(requestDb1.getId(), notNullValue());
        assertThat(requestDb2.getId(), notNullValue());
        assertThat(requestDb3.getId(), notNullValue());

        assertThat(requestDb1.getDescription(), equalTo(request1.getDescription()));
        assertThat(requestDb2.getDescription(), equalTo(request2.getDescription()));
        assertThat(requestDb3.getDescription(), equalTo(request3.getDescription()));

        assertThat(requestDb1.getRequester(), equalTo(request1.getRequester()));
        assertThat(requestDb2.getRequester(), equalTo(request2.getRequester()));
        assertThat(requestDb3.getRequester(), equalTo(request3.getRequester()));
    }

    @Test
    @DirtiesContext
    public void getRequestById() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        Request request1 = requestService.createRequest(user1.getId(), new RequestDto(null, "request1", userMapper.toUserDto(user1), null));
        Request request2 = requestService.createRequest(user1.getId(), new RequestDto(null, "request2", userMapper.toUserDto(user1), null));
        Request request3 = requestService.createRequest(user1.getId(), new RequestDto(null, "request3", userMapper.toUserDto(user1), null));

        Request requestDb1 = requestService.getRequestById(request1.getId());
        Request requestDb2 = requestService.getRequestById(request2.getId());
        Request requestDb3 = requestService.getRequestById(request3.getId());

        assertThat(requestDb1.getId(), notNullValue());
        assertThat(requestDb2.getId(), notNullValue());
        assertThat(requestDb3.getId(), notNullValue());

        assertThat(requestDb1.getDescription(), equalTo(request1.getDescription()));
        assertThat(requestDb2.getDescription(), equalTo(request2.getDescription()));
        assertThat(requestDb3.getDescription(), equalTo(request3.getDescription()));

        assertThat(requestDb1.getRequester(), equalTo(request1.getRequester()));
        assertThat(requestDb2.getRequester(), equalTo(request2.getRequester()));
        assertThat(requestDb3.getRequester(), equalTo(request3.getRequester()));
    }

    @Test
    @DirtiesContext
    public void getRequestsByUserId() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        requestService.createRequest(user1.getId(), new RequestDto(null, "request1", userMapper.toUserDto(user1), null));
        requestService.createRequest(user1.getId(), new RequestDto(null, "request2", userMapper.toUserDto(user1), null));
        requestService.createRequest(user1.getId(), new RequestDto(null, "request3", userMapper.toUserDto(user1), null));

        List<RequestDtoWithItem> requests = requestService.getRequestsByUserId(user1.getId());
        assertEquals(3, requests.size(), "Получена неверная длинна списка запросов");
    }

    @Test
    @DirtiesContext
    public void getAllRequests() {
        User user1 = userServiceImpl.createUser(new UserDto(null, "user1", "www1@mail.com"));
        User user2 = userServiceImpl.createUser(new UserDto(null, "user2", "www2@mail.com"));
        requestService.createRequest(user1.getId(), new RequestDto(null, "request1", userMapper.toUserDto(user1), null));
        requestService.createRequest(user1.getId(), new RequestDto(null, "request2", userMapper.toUserDto(user1), null));
        requestService.createRequest(user1.getId(), new RequestDto(null, "request3", userMapper.toUserDto(user1), null));

        List<RequestDto> requests = requestService.getAllRequests(user1.getId());
        assertEquals(0, requests.size(), "Получена неверная длинна списка запросов");
        List<RequestDto> requests2 = requestService.getAllRequests(user2.getId());
        assertEquals(3, requests2.size(), "Получена неверная длинна списка запросов");
    }
}
