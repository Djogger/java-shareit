package ru.practicum.server.item.service;

import ru.practicum.server.item.dto.BookingItemDto;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long ownerId);

    CommentDto addComment(Long itemId, CommentDto commentDto, Long userId);

    List<BookingItemDto> getAllItems(Long ownerId);

    BookingItemDto getItem(Long itemId, Long userId);

    List<ItemDto> searchForItem(String text);

    ItemDto editItem(Long itemId, ItemDto itemDto, Long ownerId);

}
