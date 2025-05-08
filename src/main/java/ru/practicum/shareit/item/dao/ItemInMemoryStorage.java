package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemInMemoryStorage implements ItemStorage {
    private final UserService userService;
    private final ConcurrentHashMap<Long, Item> items = new ConcurrentHashMap<>();
    private long id = 0L;

    @Override
    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        if (itemDto == null) {
            throw new NullPointerException("Тело запроса пустое");
        }

        UserDto owner = userService.getUserById(ownerId);;

        id += 1;

        itemDto.setId(id);

        Item item = ItemMapper.toItem(itemDto, owner);

        items.put(id, item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems(long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(long itemId) {
        Item item = items.get(itemId);

        if (item == null) {
            throw new NotFoundException("Вещи с ID: " + itemId + " не найдено");
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchForItem(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase().trim();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> {
                    String name = item.getName() != null ? item.getName().toLowerCase() : "";
                    String description = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
                    return name.contains(searchText) || description.contains(searchText);
                })
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto editItem(long itemId, ItemDto itemDto, long ownerId) {
        Item item = items.get(itemId);

        if (item == null) {
            throw new NotFoundException("Вещи с ID: " + itemId + " не найдено");
        }
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Редактирование доступно только владельцу");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName().trim());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription().trim());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(item);
    }
}
