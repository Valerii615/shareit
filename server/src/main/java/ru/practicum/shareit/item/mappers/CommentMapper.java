package ru.practicum.shareit.item.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.mappers.UserMapper;

@Component
@AllArgsConstructor
public class CommentMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(itemMapper.toItemDto(comment.getItem()))
                .authorName(userMapper.toUserDto(comment.getAuthor()).getName())
                .created(comment.getCreatedDate())
                .build();
    }
}
