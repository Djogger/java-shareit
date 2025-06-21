package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public class BookingItemMapper {
    public static BookingItemDto toBookingItemDto(Item item, List<Comment> comments, Booking lastBooking, Booking nextBooking) {
        List<CommentDto> commentsDto = comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();

        BookingItemDto bookingItemDto = new BookingItemDto();
        bookingItemDto.setId(item.getId());
        bookingItemDto.setName(item.getName());
        bookingItemDto.setDescription(item.getDescription());
        bookingItemDto.setAvailable(item.getAvailable());
        bookingItemDto.setLastBooking(lastBooking);
        bookingItemDto.setNextBooking(nextBooking);
        bookingItemDto.setComments(commentsDto);

        return bookingItemDto;
    }

    public static BookingItemDto itemDtoToBookingItemDto(ItemDto itemDto, List<Comment> comments, Booking lastBooking, Booking nextBooking) {
        List<CommentDto> commentsDto = comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();

        BookingItemDto bookingItemDto = new BookingItemDto();
        bookingItemDto.setId(itemDto.getId());
        bookingItemDto.setName(itemDto.getName());
        bookingItemDto.setDescription(itemDto.getDescription());
        bookingItemDto.setAvailable(itemDto.getAvailable());
        bookingItemDto.setLastBooking(lastBooking);
        bookingItemDto.setNextBooking(nextBooking);
        bookingItemDto.setComments(commentsDto);

        return bookingItemDto;
    }

}