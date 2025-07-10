package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemCommentTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private Long itemId;
    private Long userId;
    private Long bookingId;
    private Long commentId;

    private Item item;
    private User user;
    private Booking booking;
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        itemId = 1L;
        userId = 1L;
        bookingId = 1L;
        commentId = 1L;

        user = new User(userId, "Jacks", "Jacks@mail.ru");
        item = new Item(itemId, "cup", "description", true, user, null);
        booking = new Booking(bookingId, null, null, item, user, BookingStatus.WAITING);
        comment = new Comment(commentId, "i BELIEVE!", item, user, null);
    }

    @Test
    void addComment() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByBookerIdAndItemIdStatePast(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);

        CommentDto resultComment = itemService.addComment(itemId, CommentMapper.toCommentDto(comment), userId);

        assertEquals(commentId, resultComment.getId());
        assertEquals(comment.getUser().getName(), resultComment.getAuthorName());
        assertEquals(comment.getText(), resultComment.getText());
    }

}
