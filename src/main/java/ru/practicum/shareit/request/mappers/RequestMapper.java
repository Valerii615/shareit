package ru.practicum.shareit.request.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItem;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.mappers.UserMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    private final UserMapper userMapper;

    public RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requester(userMapper.toUserDto(request.getRequester()))
                .created(request.getCreated())
                .build();
    }

    public RequestDtoWithItem toDtoWithItem(Request request, List<ItemDtoRequest> items) {
        return RequestDtoWithItem.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requester(userMapper.toUserDto(request.getRequester()))
                .created(request.getCreated())
                .items(items)
                .build();
    }

}
