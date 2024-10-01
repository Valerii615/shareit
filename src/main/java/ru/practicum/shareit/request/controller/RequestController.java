package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItem;
import ru.practicum.shareit.request.mappers.RequestMapper;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private final RequestMapper requestMapper;

    @PostMapping
    public RequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody RequestDto requestDto) {
        return requestMapper.toDto(requestService.createRequest(userId, requestDto));
    }

    @GetMapping
    public List<RequestDtoWithItem> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getRequestsByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDtoWithItem getRequestsDtoWithItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable("requestId") Long requestId) {
        return requestService.getRequestDtoWithItem(userId, requestId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return null;
    }


}
