package ru.practicum.server.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.mapper.CommentMapper;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        comment = new Comment(1L, "i believe", new Item(), new User(), LocalDateTime.now());
    }

    @Test
    void toCommentDtoFromComment() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(commentDto.getCreated(), comment.getCreated());
    }

}
