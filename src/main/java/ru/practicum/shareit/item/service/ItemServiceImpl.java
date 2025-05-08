package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private ItemStorage itemStorage;

    @Override
    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        return itemStorage.addItem(itemDto, ownerId);
    }

    @Override
    public List<ItemDto> getAllItems(long ownerId) {
        return itemStorage.getAllItems(ownerId);
    }

    @Override
    public ItemDto getItem(long itemId) {
        return itemStorage.getItem(itemId);
    }

    @Override
    public List<ItemDto> searchForItem(String text) {
        return itemStorage.searchForItem(text);
    }

    @Override
    public ItemDto editItem(long itemId, ItemDto itemDto, long ownerId) {
        return itemStorage.editItem(itemId, itemDto, ownerId);
    }

}
