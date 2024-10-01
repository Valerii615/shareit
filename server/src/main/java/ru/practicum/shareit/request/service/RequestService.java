package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItem;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(Long userId, RequestDto requestDto);

    Request getRequestById(Long requestId);

    RequestDtoWithItem getRequestDtoWithItem(Long userId, Long requestId);

    List<RequestDtoWithItem> getRequestsByUserId(Long userId);

    List<RequestDto> getAllRequests(Long userId);
}
