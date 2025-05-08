package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {
    ItemDto addItem(ItemDto itemDto, long ownerId);

    List<ItemDto> getAllItems(long ownerId);

    ItemDto getItem(long itemId);

    List<ItemDto> searchForItem(String text);

    ItemDto editItem(long itemId, ItemDto itemDto, long ownerId);
}
