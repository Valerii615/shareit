package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItem;
import ru.practicum.shareit.request.mappers.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestDbStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestDbStorage requestDbStorage;
    private final UserService userService;
    private final RequestMapper requestMapper;
    private final ItemServiceImpl itemServiceImpl;
    private final ItemMapper itemMapper;


    @Override
    @Transactional
    public Request createRequest(Long userId, RequestDto requestDto) {
        log.info("Creating request for user {} with requestDto {}", userId, requestDto);
        User user = userService.findUserById(userId);
        Request request = Request.builder()
                .description(requestDto.getDescription())
                .requester(user)
                .created(LocalDateTime.now(ZoneId.systemDefault()))
                .build();
        Request requestCreated = requestDbStorage.save(request);
        log.info("Created request: {}", requestCreated);
        return requestCreated;
    }

    @Override
    public Request getRequestById(Long requestId) {
        log.info("Retrieving request with id {}", requestId);
        Request request = requestDbStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id:" + requestId + " not found"));
        log.info("Retrieved request: {}", request);
        return request;
    }

    @Override
    public RequestDtoWithItem getRequestDtoWithItem(Long userId, Long requestId) {
        log.info("Retrieving request with id {} and user {}", requestId, userId);
        User user = userService.findUserById(userId);
        Request request = getRequestById(requestId);
        List<ItemDtoRequest> itemDtoRequestList = itemServiceImpl.findItemByRequestId(requestId).stream()
                .map(itemMapper::toItemDtoRequest)
                .toList();
        RequestDtoWithItem requestDtoWithItem = requestMapper.toDtoWithItem(request, itemDtoRequestList);
        log.info("Retrieved requestDtoWithItem: {}", requestDtoWithItem);
        return requestDtoWithItem;
    }

    @Override
    public List<RequestDtoWithItem> getRequestsByUserId(Long userId) {
        log.info("Retrieving requests by user {}", userId);
        User user = userService.findUserById(userId);
        List<Request> requestList = requestDbStorage.findAllByRequesterIdOrderByCreatedDesc(userId);
        return requestList.stream()
                .map(request -> getRequestDtoWithItem(userId, request.getId()))
                .toList();
    }

    @Override
    public List<RequestDto> getAllRequests(Long userId) {
        User user = userService.findUserById(userId);
        return requestDbStorage.findAllByRequesterIdNot(userId).stream()
                .map(requestMapper::toDto)
                .toList();
    }


}
