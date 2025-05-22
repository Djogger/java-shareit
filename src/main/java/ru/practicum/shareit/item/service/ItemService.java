package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long ownerId);

    CommentDto addComment(Long itemId, CommentDto commentDto, Long userId);

    List<ItemDto> getAllItems(Long ownerId);

    BookingItemDto getItem(Long itemId, Long userId);

    List<ItemDto> searchForItem(String text);

    ItemDto editItem(Long itemId, ItemDto itemDto, Long ownerId);

}
