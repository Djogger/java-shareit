package ru.practicum.server.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;
import ru.practicum.server.item.dto.CommentDto;

@Component
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getUser().getName(),
                comment.getCreated()
        );
    }

    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .item(item)
                .user(user)
                .created(commentDto.getCreated())
                .build();
    }

}
