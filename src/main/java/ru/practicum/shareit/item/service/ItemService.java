package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long ownerId);

    List<ItemDto> getAllItems(Long ownerId);

    ItemDto getItem(Long itemId);

    List<ItemDto> searchForItem(String text);

    ItemDto editItem(Long itemId, ItemDto itemDto, Long ownerId);

}
