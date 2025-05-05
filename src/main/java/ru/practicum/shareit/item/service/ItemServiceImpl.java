package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ConcurrentHashMap<Long, Item> items = new ConcurrentHashMap<>();
    private long id = 0L;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        if (itemDto == null) {
            throw new NullPointerException("Тело запроса пустое");
        }

        UserDto owner;
        try {
            owner = userService.getUserById(ownerId);
        } catch (NotFoundException ex) {
            throw new NotFoundException("Пользователя с ID: " + ownerId + " не существует");
        }

        id += 1;

        Item item = Item.builder()
                .id(id)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(UserMapper.toUser(owner))
                .build();

        items.put(id, item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(Long itemId) {
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
    public ItemDto editItem(Long itemId, ItemDto itemDto, Long ownerId) {
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
